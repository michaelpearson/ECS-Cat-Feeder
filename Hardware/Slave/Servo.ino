Servo door1;
Servo door2;

int oldPosition = 0;

void setupServos() {
  door1.attach(A0);
  door2.attach(A1);
}

void openDoors(int position) {
  door1.attach(A0);
  door2.attach(A1);
  Serial.print("Moving to: ");
  Serial.println(position);
  int a = oldPosition;
  for(;a < position;a++) {
    setPosition(a);
  }
  for(;a > position;a--) {
    setPosition(a);
  }
  oldPosition = position;
  door1.detach();
  door2.detach();
}

void setPosition(int position) {
  Serial.println(position);
  door1.write(position);
  door2.write(position+5);
  delay(15);
}

