#define ADDRESS_STATUS          0
#define ADDRESS_SSID            1
#define ADDRESS_PASSWORD        101
#define ADDRESS_AUTHORIZED_TAG  204
#define ADDRESS_GRAM_OFFSET     208

#define MASK_CONFIGURED     (1 << 0)
#define MASK_VALID          (1 << 1)

/**
   Read the configuration status field and check for a configuration
   Returns: True if a configuration was found
*/
bool isConfigured() {
  return (EEPROM.read(ADDRESS_STATUS) & MASK_CONFIGURED) > 0;
}

/**
   Read the configuration status field and checks that the configuration has not failed previoulsy
   Returns: True if a configuration was found
*/
bool isConfigValid() {
  return (EEPROM.read(ADDRESS_STATUS) & MASK_VALID) > 0;
}


/**
   Read the value of SSID. SSID is a field starting at address 2, with a max length of 100 bytes. Terminated by "\0".
   returns the char * to null terminated string of SSID
   Address range: 2 - 102
*/
char * getSSID() {
  return (char *)EEPROM.getDataPtr() + ADDRESS_SSID;
}

/**
   Read the value of password. password is a field starting at address 102, with a max length of 100 bytes. Terminated by "\0".
   returns the char * to null terminated string of password
   Address range: 102 - 202
*/
char * getPassword() {
  return (char *)EEPROM.getDataPtr() + ADDRESS_PASSWORD;
}

uint32_t getTrustedTag() {
  return *(uint32_t *)(EEPROM.getDataPtr() + ADDRESS_AUTHORIZED_TAG);
}

void setTrustedTag(uint32_t tag) {
  *((uint32_t *)(EEPROM.getDataPtr() + ADDRESS_AUTHORIZED_TAG)) = tag;
  EEPROM.commit();
}

void setGramOffset(int32_t gramOffset) {
  *((int32_t *)(EEPROM.getDataPtr() + ADDRESS_GRAM_OFFSET)) = gramOffset;
  EEPROM.commit();
  
}

int32_t getGramOffset() {
  return *(int32_t *)(EEPROM.getDataPtr() + ADDRESS_GRAM_OFFSET);
}



/**
   Sets the value of SSID. Validates that the length of SSID + the null termination is < 100 characters and is greater then 1.
   Returns: False if the validation failed
*/
bool setSSID(char * SSID) {
  uint8_t length = strlen(SSID) + 1;
  if (length > 100 || length == 1) {
    return false;
  }
  char * ptr = getSSID();
  while ((*(ptr++) = *(SSID++)) != '\0');
  EEPROM.commit();
}

/**
   Sets the value of password. Validates that the length of password + the null termination is < 100 characters and is greater then 1.
   Returns: False if the validation failed
*/
bool setPassword(char * password) {
  uint8_t length = strlen(password) + 1;
  char * ptr = getPassword();
  if (length > 100 || length == 1) {
    *ptr = '\0';
  } else {
    while ((*(ptr++) = *(password++)) != '\0');
  }
  
  EEPROM.commit();
}

/**
   Sets the configuration status bitfield
*/
void setConfigValid(bool valid) {
  uint8_t value = EEPROM.read(ADDRESS_STATUS) & ~MASK_VALID;
  EEPROM.write(ADDRESS_STATUS, value | (valid ? MASK_VALID : 0));
  EEPROM.commit();
}

/**
   Sets the configuration status bitfield
*/
void setConfigured(bool configured) {
  uint8_t value = EEPROM.read(ADDRESS_STATUS) & ~MASK_CONFIGURED;
  EEPROM.write(ADDRESS_STATUS, value | (configured ? MASK_CONFIGURED : 0));
  EEPROM.commit();
}



