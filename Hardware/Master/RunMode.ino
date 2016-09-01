#define SERVER_CONNECTION "192.168.0.23"
#define SERVER_PORT 8080

#define COMMAND_DELIVER_FOOD 1
#define COMMAND_GET_CARD 2

WebSocketsClient socket;

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
  socket.begin(SERVER_CONNECTION, SERVER_PORT, "/ws");
  socket.onEvent(socketEvent);
}


void runModeLoop() {
  socket.loop();
  catFeederLoop();
  delay(100);
}

void socketEvent(WStype_t type, uint8_t * payload, size_t lenght) {
  StaticJsonBuffer<400> jsonBuffer;
  switch (type) {
    case WStype_CONNECTED:
      {
        Serial.printf("Connected to url: %s\n",  payload);
        JsonObject&  root = jsonBuffer.createObject();
        root["deviceId"] = ESP.getChipId();
        char buff[200];
        root.printTo(buff, sizeof(buff));
        socket.sendTXT(buff);
      }
      break;
    case WStype_TEXT:
      {
        Serial.printf("Received command: %s\n", payload);
        JsonObject& root = jsonBuffer.parseObject((char *)payload);
        executeInstruction(root);
      }
      break;
  }
}

void executeInstruction(JsonObject& payload) {
  StaticJsonBuffer<200> responseBuffer;
  switch ((int)payload["command"]) {
    case COMMAND_DELIVER_FOOD:
      {
        Serial.print("Deliver food: ");
        int gramAmount = (int)payload["gram_amount"];
        int foodIndex = (int)payload["food_type"];
        Serial.printf("Gram amount %d, Food type %d\n", gramAmount, foodIndex);
        deliverFood(gramAmount, foodIndex);
      }
      break;
    case COMMAND_GET_CARD:
      {
        char buff[200];
        JsonObject& root = responseBuffer.createObject();
        Serial.print("Get card info: ");
        uint32_t cardId = 0;
        bool isPresent = false;
        getCardInfo(&cardId, &isPresent);
        root["card_id"] = cardId;
        root["is_present"] = isPresent;
        root.printTo(buff, sizeof(buff));
        socket.sendTXT(buff);
      }
  }

}

/*
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
            Serial.print("Card requested: ");
            uint32_t cardId = 0;
            bool isPresent = false;
            getCardInfo(&cardId, &isPresent);
            write32(&client, cardId);
            write32(&client, isPresent ? 1 : 0);
            Serial.print(cardId);
            Serial.print(" Is Present ");
            Serial.println(isPresent ? "True" : "False");
          }
          break;
        // PING PONG
        case 0x03:
          {
            client.write(0x03);
          }
          break;
        //Set authorized tag
        case 0x04:
          {
            Serial.print("Setting trusted tag: ");
            int tagUID;
            if (!read32(&client, &tagUID)) {
              Serial.println("Error reading tag UID");
              error = 1;
              break;
            }
            Serial.println(tagUID);
            setTrustedTag(tagUID);
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
*/
