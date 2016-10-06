#define MOTOR_COUNT 2

int motorPins[] = {2, 3};

void stepperSetup() {
  for (int a = 0; a < MOTOR_COUNT; a++) {
    for (int b = 0; b < 4; b++) {
      pinMode(motorPins[a], OUTPUT);
    }
  }

}


void stopStepper(int index) {
  analogWrite(motorPins[index], 0);
}

void setStepperRunning(int index, bool run) {
  analogWrite(motorPins[index], run ? 255 : 0);
}




