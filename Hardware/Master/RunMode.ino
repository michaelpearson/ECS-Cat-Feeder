//#define SERVER_CONNECTION "catfeeder.herokuapp.com"
//#define SERVER_PORT 80

#define SERVER_CONNECTION "192.168.0.23"
#define SERVER_PORT 8080

#define COMMAND_DELIVER_FOOD          1
#define COMMAND_GET_CARD              2
#define COMMAND_SET_TRUSTED_CARD      3
#define COMMAND_READ_WEIGHT           4
#define COMMAND_TARE                  5

WebSocketsClient socket;

void runModeSetup() {
  char * password = getPassword();
  char * ssid = getSSID();
  Serial.println("Starting in configured mode");

  WiFi.mode(WIFI_STA);
  if (*password == '\0') {
    WiFi.begin(ssid);
  } else {
    WiFi.begin(ssid, password);
  }

  Serial.println("Connecting to network");

  uint8_t i = 0;
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
  Serial.println("Connected!");

  socket.begin(SERVER_CONNECTION, SERVER_PORT, "/ws");
  socket.onEvent(socketEvent);

  Wire.begin(2, 14);
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
  char buff[200];
  switch ((int)payload["command"]) {
    case COMMAND_DELIVER_FOOD:
      {
        Serial.println("Deliver food");
        int gramAmount = (int)payload["gram_amount"];
        int foodIndex = (int)payload["food_type"];
        int maxAmount = (int)payload["max_amount"];
        deliverFood(gramAmount, foodIndex, maxAmount);
        break;
      }
    case COMMAND_GET_CARD:
      {
        Serial.println("Get card info");
        JsonObject& root = responseBuffer.createObject();
        uint32_t cardId = 0;
        bool isPresent = false;
        getCardInfo(&cardId, &isPresent);
        root["card_id"] = cardId;
        root["is_present"] = isPresent;
        root.printTo(buff, sizeof(buff));
        socket.sendTXT(buff);
        break;
      }
    case COMMAND_SET_TRUSTED_CARD:
      {
        Serial.println("Set trusted tag");
        long tagUID = (long)payload["tag_uid"];
        setTrustedTag(tagUID);
        break;
      }
    case COMMAND_READ_WEIGHT:
      {
        Serial.println("Read weight");
        int weight = getWeight();
        JsonObject& root = responseBuffer.createObject();
        root["weight"] = weight;
        root.printTo(buff, sizeof(buff));
        socket.sendTXT(buff);
        break;
      }
    case COMMAND_TARE:
      {
        Serial.println("Tare scale");
        int currentWeight = getWeight() - getGramOffset();
        Serial.printf("Current weight: %d\n", currentWeight);
        setGramOffset(-currentWeight);
        break;
      }

  }
}

void sendMaxFoodNotification() {
  StaticJsonBuffer<50> jsonBuffer;
  char buff[50];
  JsonObject&  root = jsonBuffer.createObject();
  root["command"] = "max_food_notification";
  root.printTo(buff, sizeof(buff));
  socket.sendTXT(buff);
}

void sendTimeoutNotification(int foodIndex) {
  StaticJsonBuffer<50> jsonBuffer;
  char buff[50];
  JsonObject&  root = jsonBuffer.createObject();
  root["command"] = "food_timout_notification";
  root["food_index"] = foodIndex;
  root.printTo(buff, sizeof(buff));
  socket.sendTXT(buff);
}

