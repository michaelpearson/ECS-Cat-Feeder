#include <Q2HX711.h>

#define NUMBER_OF_FEEDERS 2
#define DELIVER_FOOD_PERIOD 1000
#define CHECK_CARD_PERIOD 150
#define DOOR_OPEN_TIMEOUT 5000
#define DELIVERY_TIMOUT 60000 //1 Minute
#define LOG_WEIGHT_PERIOD 10000 //10 seconds for testing!

#define SCALE 0.0006

int foodToDeliver[] = {0, 0};
int maxAmountOfFood = 0;
int lastWeight = 0;

long deliverFoodLastRun = 0;
long checkCardLastRun = 0;
long lastLogRun = 0;

long doorsOpenAt = 0;
bool deliveringFood = false;
long startedDeliveringAt = 0;

int learningMode = 0;

Q2HX711 scale(0, 5);

void setLearningMode(int mode) {
  learningMode = mode;
}

void catFeederLoop() {
  if (millis() - deliverFoodLastRun > DELIVER_FOOD_PERIOD) {
    deliverFoodLoop();
    deliverFoodLastRun = millis();
  }
  if (millis() - checkCardLastRun > CHECK_CARD_PERIOD) {
    checkCard();
    checkCardLastRun = millis();
  }
  if (millis() - lastLogRun > LOG_WEIGHT_PERIOD) {
    sendLogWeightCommand(getWeight());
    lastLogRun = millis();
  }
}


int getWeight() {
  int weight = (scale.read() * SCALE) + getGramOffset();
  //Serial.print("Weight: ");
  //Serial.println(weight);
  return weight;
}

void checkCard() {
  if (deliveringFood) {
    return;
  }
  uint32_t id;
  bool present;
  getCardInfo(&id, &present);
  if (present) {
    Serial.print("Card detected: ");
    Serial.println(id);
    if (id == getTrustedTag()) {
      doorsOpenAt = millis();
      Serial.println("Opening doors");
      openDoors(true);
      logAccess(true);
    } else {
      logAccess(false);
    }
  }
  if (doorsOpenAt != 0 && millis() - doorsOpenAt > DOOR_OPEN_TIMEOUT) {
    Serial.println("Closing doors");
    openDoors(false);
    doorsOpenAt = 0;
  }
}

void deliverFoodLoop() {
  bool shouldDeliverFood = false;
  int a;

  for (a = 0; a < NUMBER_OF_FEEDERS; a++) {
    if (foodToDeliver[a] > 0) {
      shouldDeliverFood = true;
      break;
    }
  }

  if (shouldDeliverFood) {
    for (int b = 0; b < NUMBER_OF_FEEDERS; b++) {
      Serial.printf("Food %d, amount: %d\n", b, foodToDeliver[b]);
    }
  } else {
    return;
  }

  int weight = getWeight();
  if (shouldDeliverFood && !deliveringFood) {
    Serial.println("Resetting last weight");
    lastWeight = weight;
  }


  foodToDeliver[a] -= weight - lastWeight;
  
  Serial.print("Change in weight: ");
  Serial.print(weight - lastWeight);
  Serial.print(" Remaning: ");
  Serial.println(foodToDeliver[a]);

  lastWeight = weight;
  
  if (foodToDeliver[a] <= 0) {
    Serial.println("Complete!");
    deliveringFood = false;
    foodToDeliver[a] = 0;
    stopAllConveyers();
    return;
  }

  if (weight >= maxAmountOfFood) {
    Serial.println("Over max!");
    Serial.println(maxAmountOfFood);
    if (deliveringFood) {
      stopAllConveyers();
      sendMaxFoodNotification();
      deliveringFood = false;
      for (int b = 0; b < NUMBER_OF_FEEDERS; b++) {
        foodToDeliver[b] = 0;
      }
      return;
    }
  }

  if (!deliveringFood) {
    startedDeliveringAt = millis();
    deliveringFood = true;
    runConveyer(a, true);
  }

  if (millis() - startedDeliveringAt > DELIVERY_TIMOUT) {
    foodToDeliver[a] = 0;
    sendTimeoutNotification(a);
    stopAllConveyers();
    deliveringFood = false;
  }

}

void stopAllConveyers() {
  for (int a = 0; a < NUMBER_OF_FEEDERS; a++) {
    runConveyer(a, false);
  }
}

void runAllConveyers() {
  for (int a = 0; a < NUMBER_OF_FEEDERS; a++) {
    runConveyer(a, true);
  }
}

void deliverFood(int gramAmount, int foodType, int max) {
  Serial.printf("Deliver %d grams of food %d\n", gramAmount, foodType);
  maxAmountOfFood = max;
  if (!(foodType >= 0 && foodType < 2)) {
    return;
  }
  foodToDeliver[foodType] += gramAmount;
  Serial.printf("Remaining food to deliver: %d\n", foodToDeliver[foodType]);
}

void runConveyer(int index, bool run) {
  Wire.beginTransmission(8);
  Wire.write(0x01);
  Wire.write(index & 0xFF);
  Wire.write(run ? 0x01 : 0x00);
  Wire.endTransmission();
  Serial.printf("Conveyer %d is %s.\n", index, run ? "running" : "stopped");
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
  int position = 0;

  switch (learningMode) {
    case 0:
    default:
      position = open ? 85 : 5;
      break;
    case 1:
      position = open ? 85 : 40;
      break;
    case 2:
      position = open ? 85 : 60;
      break;
  }

  Wire.beginTransmission(8);
  Wire.write(0x03);
  Wire.write(position);
  Wire.endTransmission();
}

