#define TIMEOUT 1000

void write32(WiFiClient * client, int32_t number) {
  client->write((byte)(number >> (0 * 8)));
  client->write((byte)(number >> (1 * 8)));
  client->write((byte)(number >> (2 * 8)));
  client->write((byte)(number >> (3 * 8)));
}

bool read32(WiFiClient * client, int32_t * number) {
  unsigned long timeout = millis();
  while(client->available() < 4) {
    if(millis() - timeout > TIMEOUT) {
      return false;
    }
  }
  *number = (int32_t)client->read();
  *number |= (int32_t)client->read() << (1 * 8);
  *number |= (int32_t)client->read() << (2 * 8);
  *number |= (int32_t)client->read() << (3 * 8);
  return true;
}

