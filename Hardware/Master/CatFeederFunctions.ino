#define NUMBER_OF_FEEDERS 2
#define DELIVER_FOOD_PERIOD 1000
#define CHECK_CARD_PERIOD 150
#define DOOR_OPEN_TIMEOUT 5000

int foodToDeliver[] = {0, 0};
int lastWeight = 0;

long deliverFoodLastRun = 0;
long checkCardLastRun = 0;
long doorsOpenAt = 0;


void catFeederLoop() {
  if(millis() - deliverFoodLastRun > DELIVER_FOOD_PERIOD) {
    deliverFoodLoop();
    deliverFoodLastRun = millis();
  }
  if(millis() - checkCardLastRun > CHECK_CARD_PERIOD) {
    checkCard();
    checkCardLastRun = millis();
  }
}


int getWeight() {
  return 0;
}

void checkCard() {
  uint32_t id;
  bool present;
  getCardInfo(&id, &present);
  if(present) {
    Serial.print("Card detected: ");
    Serial.println(id);
    if(id == getTrustedTag()) {
      doorsOpenAt = millis();
      Serial.println("Opening doors");
      openDoors(true);
    }
  }
  if(doorsOpenAt != 0 && millis() - doorsOpenAt > DOOR_OPEN_TIMEOUT) {
    Serial.println("Closing doors");
    openDoors(false);
    doorsOpenAt = 0;
  }
}

void deliverFoodLoop() {
  bool deliverFood = false;
  int a;
  for(a = 0;a < NUMBER_OF_FEEDERS;a++) {
    if(foodToDeliver[a] > 0) {
      deliverFood = true;
      break;
    }
  }
  if(!deliverFood) {
    return;
  }
  runConveyer(a, true);
  int weight = getWeight();
  foodToDeliver[a] -= weight - lastWeight;
  lastWeight = weight;
  if(foodToDeliver[a] == 0) {
    runConveyer(a, false);
  }
}

void deliverFood(int gramAmount, int foodType) {
  Serial.printf("Deliver %d grams of food %d\n", gramAmount, foodType);
  if(!(foodType >= 0 && foodType < 2)) {
    return;
  }
  foodToDeliver[foodType] += gramAmount;
  Serial.printf("Remaining food to deliver: %d\n", foodToDeliver[foodType]);
  if(foodToDeliver[foodType] == 0) {
    runConveyer(foodType, false);
  }
}

void runConveyer(int index, bool run) {
  Wire.beginTransmission(8);
  Wire.write(0x01);
  Wire.write(index & 0xFF);
  Wire.write(run ? 0x01 : 0x00);
  Wire.endTransmission();
  //Serial.printf("Conveyer %d is %s.\n", index, run ? "running" : "stopped");
}

void getCardInfo(uint32_t * cardId, bool * isPresent) {
  uint32_t id = 0;
  Wire.requestFrom(8, 5); 
  id |= (Wire.read() & 0xFF) << (0 * 8);
  id |= (Wire.read() & 0xFF) << (1 * 8);
  id |= (Wire.read() & 0xFF) << (2 * 8);
  id |= (Wire.read() & 0xFF) << (3 * 8);
  *cardId = id;
  *isPresent = Wire.read() > 0;
}

void openDoors(bool open) {
  Wire.beginTransmission(8);
  Wire.write(0x03);
  Wire.write(open ? 0x01 : 0x00);
  Wire.endTransmission();
}
