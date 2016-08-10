#define NUMBER_OF_FEEDERS 2

int foodToDeliver[] = {0, 0};
int lastWeight = 0;

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


void catFeederLoop() {
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


int getWeight() {
  return 0;
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
  Serial.print("Card requested: ");
  Serial.print(*cardId);
  Serial.print(" Is Present ");
  Serial.println(*isPresent ? "True" : "False");
}

