#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include "DNSServer.h"

#define SSID "Captive Portal"
#define PASSWORD "password"

ESP8266WebServer server(80);
IPAddress ip(192, 168, 1,1);
DNSServer dnsServer;

const int led = 13;

void handleRedirect() {
  server.sendHeader("Location", "http://192.168.1.1/setup");
  server.send(301, "text/plain", "Redirecting...");
}
void handleSetup() {
  server.send(200, "text/plain", "Hello World!");
}

void setup(void){
  WiFi.mode(WIFI_AP);
  WiFi.softAPConfig(ip, ip, IPAddress(255, 255, 255, 0));
  WiFi.softAP(SSID, PASSWORD);
 dnsServer.start(53, "*", ip);

 
  Serial.begin(115200);
  Serial.println("");

  server.on("/", handleRedirect);
  server.onNotFound(handleRedirect);
  server.on("/setup", handleSetup);

  server.begin();
  Serial.println("HTTP server started");
}

void loop(void) {
  dnsServer.processNextRequest();
  server.handleClient();
}
