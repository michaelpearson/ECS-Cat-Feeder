IPAddress ip(192, 168, 1, 1);
ESP8266WebServer server(80);
DNSServer dnsServer;

#define DEFAULT_SSID "ECS Cat feeder"

void setupModeSetup() {
  Serial.println("Starting in non-configured mode");

  WiFi.mode(WIFI_AP);
  WiFi.softAPConfig(ip, IPAddress(0, 0, 0, 0), IPAddress(255, 255, 255, 0));
  WiFi.softAP(DEFAULT_SSID);
  WiFi.scanNetworks(true);

  SPIFFS.begin();

  dnsServer.start(53, "*", ip);

  server.on("/info", handleInfo);
  server.on("/save", handleSave);


  server.on("/setup", handleSetup);
  server.on("/jquery.js", handleJquery);
  server.on("/loading.svg", handleLoading);
  server.on("/wifi.png", handleWifi);

  server.onNotFound(handleRedirect);
  server.begin();
  Serial.println("Ready");
}

void setupModeLoop() {
  dnsServer.processNextRequest();
  server.handleClient();
}


void handleSetup() {
  sendFile("/setup.html.gz", "text/html");
}

void handleJquery() {
  sendFile("/jquery.js.gz", "application/javascript");
}

void handleWifi() {
  sendFile("/wifi.png.gz", "image/png");
}

void handleLoading() {
  sendFile("/loading.svg.gz", "image/svg+xml");
}


void sendFile(char * path, char * contentType) {
  File file = SPIFFS.open(path, "r");
  size_t sent = server.streamFile(file, contentType);
  file.close();
}

void handleInfo() {
  int n = WiFi.scanComplete();
  String data = "{ \"scan\":[";
  for (uint8_t a = 0; a < n; a++) {
    data += "{\"network\":\"" + WiFi.SSID(a) + "\",\"encryption\":" + WiFi.encryptionType(a) + ",\"RSSI\": " + WiFi.RSSI(a) + "}";
    if (a < n - 1) {
      data += ",";
    }
  }
  data += "], \"status\":{\"configuredSSID\":\"" + String(getSSID()) + "\", \"configFailed\":" + (isConfigValid() ? "false" : "true") + "}}";
  server.send(200, "application/json", data);
  Serial.println("End response");
  if (n > 0 || n == WIFI_SCAN_FAILED) {
    WiFi.scanNetworks(true);
  }
}

void handleSave() {
  char buff[100];
  server.arg("password").toCharArray(buff, 100);
  setPassword(buff);
  server.arg("ssid").toCharArray(buff, 100);
  setSSID(buff);
  setConfigValid(true);
  setConfigured(true);
  sendFile("/saved.html.gz", "text/html");
  delay(100);
  ESP.restart();
}

void handleRedirect() {
  server.sendHeader("Location", "http://192.168.1.1/setup");
  server.send(301, "text/plain", "Redirecting...");
}
