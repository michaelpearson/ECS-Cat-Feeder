#define RST_PIN         48
#define SS_PIN          53
#define TIMEOUT         1000

MFRC522 mfrc522(SS_PIN, RST_PIN);

uint32_t lastCard = 0;
long lastSeen = 0;

void readerSetup() {
  SPI.begin();
  mfrc522.PCD_Init();
  mfrc522.PCD_DumpVersionToSerial();
  mfrc522.PCD_SetAntennaGain(mfrc522.RxGain_max);

  Wire.onRequest(requestEvent);
}


void readerLoop() {
  if (!mfrc522.PICC_IsNewCardPresent()) {
    return;
  }

  if(millis() - lastSeen < TIMEOUT) {
    //If we have recently seen the card then dont waste time reading its uid
    lastSeen = millis();
    return; 
  }

  if (!mfrc522.PICC_ReadCardSerial()) {
    return;
  }

  lastSeen = millis();
  lastCard = 0;
  for (byte i = 0; i < mfrc522.uid.size; i++) {
    lastCard |= mfrc522.uid.uidByte[i] << (mfrc522.uid.size - i);
  }
}


void requestEvent() {
  uint8_t buff[5];
  buff[0] = (((uint32_t)lastCard >> (0 * 8)) & 0xFF);
  buff[1] = (((uint32_t)lastCard >> (1 * 8)) & 0xFF);
  buff[2] = (((uint32_t)lastCard >> (2 * 8)) & 0xFF);
  buff[3] = (((uint32_t)lastCard >> (3 * 8)) & 0xFF);
  buff[4] = (millis() - lastSeen < TIMEOUT) ? 0x01 : 0x00;
  Wire.write(buff, 5);
}
