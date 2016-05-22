void runModeSetup() {
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
}

void runModeLoop() {
  Serial.println("Here");
  delay(500);
}

