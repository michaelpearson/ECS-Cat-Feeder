Servo door1;
Servo door2;

void setupServos() {
  door1.attach(8);
  door2.attach(9);
}

void openDoors(int position) {
  Serial.printf("Opening doors to: %d\n", position);
  door1.write(position);
  door2.write(180 - position);
}

