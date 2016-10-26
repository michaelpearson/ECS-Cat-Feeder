#define MOTOR_COUNT 2

int motorPins[] = {2, 3};
bool motors[] = {false, false};
int loopTime = 10;
bool loopState = false;

void motorsSetup() {
  for (int a = 0; a < MOTOR_COUNT; a++) {
    for (int b = 0; b < 4; b++) {
      pinMode(motorPins[a], OUTPUT);
    }
  }
}

void motorLoop() {
  loopTime--;
  if(loopTime == 0) {
    loopState = !loopState;
    loopTime = loopState ? 1 : 20; //N iterations at 10ms per iteration = 300ms
  }
  
  for(int a = 0;a < MOTOR_COUNT;a++) {
    digitalWrite(motorPins[a], loopState && motors[a]);
  }
}

void setMotorsRunning(int index, bool run) {
  motors[index] = run;
}




