#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <EEPROM.h>
#include <DNSServer.h>
#include <FS.h>

bool configMode;

void setup() {
  Serial.begin(115200);
  Serial.print("\n");
  EEPROM.begin(512);

  delay(1000);

  if (digitalRead(0) == 1 && isConfigured() && isConfigValid()) {
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
