#include <WebSockets.h>
#include <WebSocketsClient.h>
#include <ArduinoJson.h>
#include <ESP8266HTTPClient.h>
#include <Wire.h>
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <EEPROM.h>
#include <DNSServer.h>
#include <FS.h>


bool configMode;

void setup() {
  Serial.begin(115200);
  Serial.println("\nInit...");
  EEPROM.begin(512);
  
  if (isConfigured() && isConfigValid()) {
    configMode = false;
    runModeSetup();
  } else {
    configMode = true;
    setupModeSetup();
  }
}

void loop() {
  if (configMode) {
    setupModeLoop();
  } else {
    runModeLoop();
  }
}
