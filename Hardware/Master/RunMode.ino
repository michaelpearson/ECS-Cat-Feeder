#define SERVER_CONNECTION "catfeeder.herokuapp.com"
WiFiClient client;

void runModeSetup() {
  char * password = getPassword();
  char * ssid = getSSID();
  Serial.println("Starting in configured mode");
  Serial.print("WiFi network SSID: ");
  Serial.println(ssid);
  if (*password != '\0') {
    Serial.print("WiFi network password: ");
    Serial.println(password);
  }

  WiFi.mode(WIFI_STA);
  if (*password == '\0') {
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
  HTTPClient http;
  StaticJsonBuffer<200> jsonBuffer;
  char buff[100];
  Serial.println("Requesting URL to connect to");
  sprintf(buff, "http://%s/api/feeder/%d/url", SERVER_CONNECTION, ESP.getChipId());
  Serial.print("Requesting: ");
  Serial.println(buff);
  http.begin(buff);
  int httpCode = http.GET();
  Serial.print("Response code: ");
  Serial.println(httpCode);
  if(httpCode != 200) {
    Serial.println("Could not get url to connect to");
    delay(1000);
    return;
  }
  JsonObject& root = jsonBuffer.parseObject(http.getString());
  if(!root.success()) {
    Serial.println("Could not decode json");
    delay(1000);
    return;
  }
  Serial.printf("Connecting to %s, on port: %d\n", (const char *)root["host"], (int)root["port"]);
  if (!client.connect((const char *)root["host"], (int)root["port"])) {
    Serial.println("connection failed");
    delay(1000);
  } else {
    write32(&client, ESP.getChipId());
  }
}

void runModeLoop() {
  if (!client.connected()) {
    connectClient();
  } else {
    if (client.available()) {
      bool error = false;
      switch (client.read()) {
        //Deliver food
        case 0x01:
          {
            int gramAmount, foodType;
            error |= !read32(&client, &gramAmount);
            error |= !read32(&client, &foodType);
            if (!error) {
              deliverFood(gramAmount, foodType);
            }
          }
          break;
        //Get last card UID
        case 0x02:
          {
            Serial.println("Ger card");
            uint32_t cardId = 0;
            bool isPresent = false;
            getCardInfo(&cardId, &isPresent);
            write32(&client, cardId);
            write32(&client, isPresent ? 1 : 0);
          }
          break;
        // PING PONG
        case 0x03:
          {
            client.write(0x03);
          }
          break;
      }
      if (error) {
        Serial.println("There was an error processing the request");
      }
    }
    catFeederLoop();
    delay(100);
  }
}
