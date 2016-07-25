#define SERVER_CONNECTION "10.140.130.32"
#define SERVER_PORT 6969

WiFiClient client;
int32_t number = 0;

void runModeSetup() {
  char * password = getPassword();
  char * ssid = getSSID();
  Serial.println("Starting in configured mode");
  Serial.print("WiFi network SSID: ");
  Serial.println(ssid);
  if(*password != '\0') {
    Serial.print("WiFi network password: ");
    Serial.println(password);
  }

  WiFi.mode(WIFI_STA);
  if(*password == '\0') {
    WiFi.begin(ssid);
  } else {
    WiFi.begin(ssid, password);
  }
  
  uint8_t i = 0;
  Serial.println("Connecting to network");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
    if (++i == 50) {
      setConfigValid(false);
      delay(100);
      ESP.restart();
    }
  }
  Serial.println();
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());

  connectClient();
  write32(&client, ESP.getChipId());
  Serial.println("Ready");
}

void connectClient() {
  if (!client.connect(SERVER_CONNECTION, SERVER_PORT)) {
    Serial.println("connection failed");
    //ESP.restart();
  }
}

void runModeLoop() {
  if(!client.connected()) {
    connectClient();
  } else {
    write32(&client, number++);
  }
  
  delay(50);
}

