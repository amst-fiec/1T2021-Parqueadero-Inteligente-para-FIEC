#include <NewPing.h>
#include <TimeLib.h>
#include <SPI.h>
#include <MFRC522.h>
#include <WISOL.h>
#include <Tsensors.h>
#include <Wire.h>
Isigfox *Isigfox = new WISOL();

//Variables para el lector RFID
#define RST_PIN  9    //Pin 9 para el reset del RC522
#define SS_PIN  10   //Pin 10 para el SS (SDA) del RC522
MFRC522 mfrc522(SS_PIN, RST_PIN); //Creamos el objeto para el RC522
byte ActualUID[4]; //almacenará el código del Tag leído
byte Usuario_privilegiado[4]= {0x9D, 0x85, 0x4B, 0x69} ; //código del usuario 1
byte Usuario_normal[4]= {0xC2, 0x1E, 0x3C, 0x69} ; //código del usuario 2

//Variables para los leds
const int ledPIN1 = 4;
const int ledPIN2 = 5;
const int ledPIN3 = 6;
const int ledPIN4 = 7;

//Variables para la medicion con el sensor y envio del estado
int temp;
int medicion;
#define TRIGGER_PIN  8
#define ECHO_PIN     3
#define MAX_DISTANCE 200
NewPing sonar(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE);
const int vehiculo_Presente = 50;
int banderaAnterior = 0;
int bandera = 0;

typedef union {
uint16_t number;
uint8_t bytes[2];
} UINT16_t;

void setup() {
  int flagInit;
  Serial.begin(9600);
  flagInit = -1;
  while (flagInit == -1) {
    Serial.println(""); // Make a clean restart
    delay(1000);
    flagInit = Isigfox->initSigfox();
    Isigfox->testComms();
    //Isigfox->setPublicKey(); // set public key for usage with SNEK
  }
  delay(1000);
  SPI.begin();        //Iniciamos el Bus SPI
  mfrc522.PCD_Init(); // Iniciamos  el MFRC522
  pinMode(ledPIN1 , OUTPUT);
  pinMode(ledPIN2 , OUTPUT);
  pinMode(ledPIN3 , OUTPUT);
  pinMode(ledPIN4 , OUTPUT);

}

void loop() {
  //Porcentaje de carga bateria
  if (hour()<=15){
    digitalWrite(ledPIN3 , HIGH);   // poner el Pin en HIGH         
    digitalWrite(ledPIN2 , LOW);    // poner el Pin en LOW
    digitalWrite(ledPIN1 , LOW);    // poner el Pin en LOW
  }
  if (hour()>15 && hour()<=19 ){
    digitalWrite(ledPIN3 , LOW);   // poner el Pin en LOW       
    digitalWrite(ledPIN2 , HIGH);    // poner el Pin en HIGH
    digitalWrite(ledPIN1 , LOW);    // poner el Pin en LOW
  }
  if (hour()>19){
    digitalWrite(ledPIN3 , LOW);   // poner el Pin en LOW       
    digitalWrite(ledPIN2 , LOW);    // poner el Pin en LOW
    digitalWrite(ledPIN1 , HIGH);    // poner el Pin en HIGH
  }
  
  //Medicion de vehiculo presente
  if (second()%20==0 && second()!=0){ 
    temp = second();
    while(temp == second()){
       medicion = sonar.ping_median();
    }
    Serial.print("Distancia:");
    Serial.println(medicion);
  }
  if (medicion <= vehiculo_Presente ){
    bandera = 1;
  }else{
    bandera = 0;
  }
  
  if (bandera != banderaAnterior){
    Serial.println("Realizando envio de estado del parqueadero");
    if (bandera == 1){
      Serial.print("Se esta parqueando un vehiculo, estado del parqueo: ");
      Serial.println(bandera);
    }else{
      Serial.print("Parqueo libre, estado del parqueo: ");
      Serial.println(bandera);
    }
    //ejecuto sigfox
    byte *int_byte = (byte *)bandera;
    //indicamos el tamaño de nuestro mensaje sabiendo que el máximo tamaño es de 12 bytes
    const uint8_t payloadSize = 5;
    uint8_t buf_str[payloadSize];
    buf_str[0] = int_byte[0];
    buf_str[1] = int_byte[1];
    buf_str[2] = int_byte[2];
    buf_str[3] = int_byte[3];
    Send_Pload(buf_str, payloadSize);
    // Wait 5s
    temp = second();
    Serial.print("enviando");
    int valor1 = temp;
    while(second()<=temp+5){
      if(valor1!=second()){
        Serial.print(".");
        valor1 = second();
      }
    }
    Serial.println("");
    banderaAnterior = bandera;
  }
  //Alerta usuario normal entra a parqueo privilegeado
  //Se Valida usuario normal y se envia alarma(enciendo el led azul por 3 segundos)
  if ( mfrc522.PICC_IsNewCardPresent()) 
        {  
      //Seleccionamos una tarjeta
            if ( mfrc522.PICC_ReadCardSerial()) 
            {
                  // Enviamos serialemente su UID
                  Serial.print("Card UID:");
                  for (byte i = 0; i < mfrc522.uid.size; i++) {
                          Serial.print(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " ");
                          Serial.print(mfrc522.uid.uidByte[i], HEX);   
                  } 
                  Serial.print("         ");
                  if(compareArray(ActualUID,Usuario_privilegiado))
                    Serial.println("Acceso de usuario Privilegiado");
                  else if(compareArray(ActualUID,Usuario_normal)){
                    Serial.println("Acceso de usuario Normal");
                    temp = second();
                    while(temp <= temp+3){
                      digitalWrite(ledPIN4 , HIGH);   // poner el Pin en HIGH  
                    }
                    digitalWrite(ledPIN4 , LOW);    // poner el Pin en LOW
                  }else{
                    Serial.println("Acceso denegado...");
                  }   
                  // Terminamos la lectura de la tarjeta  actual
                  mfrc522.PICC_HaltA();         
            }      
  } 
}

void Send_Pload(uint8_t *sendData, const uint8_t len){
recvMsg *RecvMsg;
RecvMsg = (recvMsg *)malloc(sizeof(recvMsg));
Isigfox->sendPayload(sendData, len, 0, RecvMsg);
for (int i = 0; i < RecvMsg->len; i++) {
Serial.print(RecvMsg->inData[i]);
}
Serial.println("");
free(RecvMsg);
}
//Función para comparar dos vectores
 boolean compareArray(byte array1[],byte array2[])
{
  if(array1[0] != array2[0])return(false);
  if(array1[1] != array2[1])return(false);
  if(array1[2] != array2[2])return(false);
  if(array1[3] != array2[3])return(false);
  return(true);
}
