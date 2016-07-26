#define SERVER_CONNECTION "192.168.0.23"
#define SERVER_PORT 6969

WiFiClient client;

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

  Wire.begin(2, 14);
  
  Serial.println("Ready");
}

void connectClient() {
  if (!client.connect(SERVER_CONNECTION, SERVER_PORT)) {
    Serial.println("connection failed");
    delay(1000);
  } else {
    write32(&client, ESP.getChipId());
  }
}

void runModeLoop() {
  if(!client.connected()) {
    connectClient();
  } else {
    if(client.available()) {
      bool error = false;
      switch(client.read()) {
        //Deliver food
        case 0x01:
          int gramAmount, foodType;
          error |= !read32(&client, &gramAmount);
          error |= !read32(&client, &foodType);
          if(!error) {
            deliverFood(gramAmount, foodType);
          }
          break;
      }
      if(error) {
        Serial.println("There was an error processing the request");
      }
    }
    catFeederLoop();
    delay(100);
  }
}
