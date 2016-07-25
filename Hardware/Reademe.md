# Software for the embedded portion of the project.
There are two sketches for the control of the cat feeder. Master is the code that runs on the ESP8266 and handles the control of the system. The ESP does not have enough GPIO's so the slave is used to interface with the hardware.

Master will run on an ESP8266 with *512K* of flash. To upload the flash, install the esp board into the arduino IDE (https://github.com/esp8266/Arduino)
Select:
Board: generic ESP8266
Upload Mode: nodemcu
Upload speed: max
flash size: 128k spiffs


The slave will run on almost any arduino or arduino clone and communicates with the master through I2C. 
