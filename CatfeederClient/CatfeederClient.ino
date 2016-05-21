#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <EEPROM.h>
#include <DNSServer.h>

#define DEFAULT_SSID "ECS Cat feeder"

ESP8266WebServer server(80);
IPAddress ip(192, 168, 1, 1);
DNSServer dnsServer;

bool configMode;

extern unsigned char html_setup_html[];
extern unsigned char html_scripts_js[];
extern unsigned char html_saved_html[];


void handleRedirect() {
  server.sendHeader("Location", "http://192.168.1.1/setup");
  server.send(301, "text/plain", "Redirecting...");
}
void handleSetup() {
  Serial.println("Sending setup...");
  server.send(200, "text/html", (char *)html_setup_html);
}

void handleScripts() {
  Serial.println("Sending scripts...");
  server.send(200, "application/javascript", (char *)html_scripts_js);
}

void handleScan() {
  Serial.println("Scanning...");
  int n = WiFi.scanNetworks();
  String data = "[";
  for (uint8_t a = 0; a < n; a++) {
    data += "{\"network\":\"" + WiFi.SSID(a) + "\",\"encryption\":" + WiFi.encryptionType(a) + "}";
    if (a < n - 1) {
      data += ",";
    }
  }
  data += "]";
  server.send(200, "application/json", data);
}

void handleSave() {
  char buff[100];
  server.arg("password").toCharArray(buff, 100);
  setPassword(buff);
  server.arg("ssid").toCharArray(buff, 100);
  setSSID(buff);
  setConfigValid(true);
  setConfigured(true);
  server.send(200, "text/html", (char *)html_saved_html);
  server.close();
  delay(100);
  Serial.println(isConfigured());
  Serial.println(isConfigValid());
  ESP.restart();
}

void setup() {
  Serial.begin(115200);
  Serial.print("\n");
  EEPROM.begin(512);

  if (isConfigured() && isConfigValid()) {
    configMode = false;
    Serial.println("Starting in configured mode");

    Serial.print("WiFi network SSID: ");
    Serial.println(getSSID());
    Serial.print("WiFi network password: ");
    Serial.println(getPassword());

    WiFi.mode(WIFI_STA);
    WiFi.begin(getSSID(), getPassword());
    uint8_t i = 0;
    Serial.println("Connecting to network");
    while (WiFi.status() != WL_CONNECTED) {
      delay(500);
      Serial.print(".");
      if (++i == 20) {
        setConfigValid(false);
        delay(100);
        ESP.restart();
      }
    }
    Serial.println();
    Serial.print("IP Address: ");
    Serial.println(WiFi.localIP());
    Serial.println("Ready");
    
  } else {
    configMode = true;
    Serial.println("Starting in non-configured mode");

    WiFi.mode(WIFI_AP);
    WiFi.softAPConfig(ip, ip, IPAddress(255, 255, 255, 0));
    WiFi.softAP(DEFAULT_SSID);

    dnsServer.start(53, "*", ip);

    server.on("/", handleRedirect);
    server.onNotFound(handleRedirect);
    server.on("/setup", handleSetup);
    server.on("/scripts.js", handleScripts);
    server.on("/scan", handleScan);
    server.on("/save", handleSave);
    server.begin();
    Serial.println("Ready");
  }
}

void loop() {
  if(configMode) {
    dnsServer.processNextRequest();
    server.handleClient();
  } else {
    Serial.println("Here");
    delay(100);
  }
}
