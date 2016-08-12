Servo door1;
Servo door2;

void setupServos() {
  door1.attach(8);
  door2.attach(9);
}

void openDoors(bool open) {
  Serial.print("Opening doors: ");
  Serial.println(open ? "true" : "false");
  if(open) {
    door1.write(0);
    door2.write(180);
  } else {
    door1.write(180);
    door2.write(0);
  }
}

