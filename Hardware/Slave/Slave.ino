#include <Wire.h>

#define STEPPER_COUNT 1

typedef struct {
  int pins[4];
  bool running;
  int position;
} Stepper;

Stepper steppers[STEPPER_COUNT];

void setup() {
  steppers[0].pins[0] = 30;
  steppers[0].pins[1] = 28;
  steppers[0].pins[2] = 26;
  steppers[0].pins[3] = 24;
  steppers[0].running = false;
  steppers[0].position = 0;

  for(int a = 0;a < STEPPER_COUNT;a++) {
    for(int b = 0;b < 4;b++) {
      pinMode(steppers[a].pins[b], OUTPUT);
    }
  }
  
  Wire.begin(8);
  Wire.onReceive(receiveEvent);

  Serial.begin(115200);
  Serial.println("Init...");
  
}

void receiveEvent(int howMany) {
  switch (Wire.read()) {
    case 0x01:
      uint8_t index = Wire.read();
      bool run = Wire.read() == 0x01;
      steppers[index].running = run;
      break;
  }
}

void loop() {
  for(int a = 0;a < STEPPER_COUNT;a++) {
    if(steppers[a].running) {
      Serial.println("Stepper running...");
      steppers[a].position = (steppers[a].position + 1) % 4;
      setPosition(&steppers[a]);
    } else {
      stopStepper(&steppers[a]);
    }
  }
  delay(10);
}

void setPosition(Stepper * stepper) {
  digitalWrite(stepper->pins[0], stepper->position == 0 ? HIGH : LOW);
  digitalWrite(stepper->pins[1], stepper->position == 1 ? HIGH : LOW);
  digitalWrite(stepper->pins[2], stepper->position == 2 ? HIGH : LOW);
  digitalWrite(stepper->pins[3], stepper->position == 3 ? HIGH : LOW);
}

void stopStepper(Stepper * stepper) {
  digitalWrite(stepper->pins[0], LOW);
  digitalWrite(stepper->pins[1], LOW);
  digitalWrite(stepper->pins[2], LOW);
  digitalWrite(stepper->pins[3], LOW);
}

