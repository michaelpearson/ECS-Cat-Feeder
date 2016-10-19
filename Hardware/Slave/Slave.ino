#include <Servo.h>
#include <SPI.h>
#include <MFRC522.h>
#include <Wire.h>

void setup() {
  Wire.begin(8);
  Wire.onReceive(receiveEvent);

  Serial.begin(115200);
  Serial.println("Init...");

  motorsSetup();
  readerSetup();
  setupServos();
}

int position = 0;

void receiveEvent(int howMany) {
  switch (Wire.read()) {
    //Run motor
    case 0x01:
      {
        uint8_t index = Wire.read();
        bool run = Wire.read() == 0x01;
        setMotorsRunning(index, run);
      }
      break;
    //Request card data (this is the only read command at the moment so we dont need to store the command)
    case 0x02:
      break;
    //Open the doors
    case 0x03:
      position = Wire.read();
      Serial.println(position);
      break;
  }
}

void loop() {
  readerLoop();
  openDoors(position);
  motorLoop();
  delay(10);
}


