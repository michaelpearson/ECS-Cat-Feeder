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
}

void receiveEvent(int howMany) {
  switch (Wire.read()) {
    case 0x01:
      {
        uint8_t index = Wire.read();
        bool run = Wire.read() == 0x01;
        setStepperRunning(index, run);
      }
      break;
    case 0x02:
      break;
  }
}

void loop() {
  stepperLoop();
  readerLoop();
  delay(10);
}


