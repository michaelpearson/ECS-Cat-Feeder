#include <Servo.h>
#include <SPI.h>
#include <MFRC522.h>
#include <Wire.h>

void setup() {
  Wire.begin(8);
  Wire.onReceive(receiveEvent);

  Serial.begin(115200);
  Serial.println("Init...");

  stepperSetup();
  readerSetup();
  setupServos();
}

void receiveEvent(int howMany) {
  switch (Wire.read()) {
    //Run motor
    case 0x01:
      {
        uint8_t index = Wire.read();
        bool run = Wire.read() == 0x01;
        setStepperRunning(index, run);
      }
      break;
    //Request card data (this is the only read command at the moment so we dont need to store the command)
    case 0x02:
      break;
    //Open the doors
    case 0x03:
      openDoors(Wire.read() > 0);
      break;
  }
}

void loop() {
  stepperLoop();
  readerLoop();
  delay(10);
}


