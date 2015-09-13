 #include <SoftwareSerial.h>
 //Pines del Bluetooth
 #define TX 14
 #define RX 15
 //Creamos la conexion con el Bluetooth
 SoftwareSerial bt(TX, RX);
 // Contador
 int i = 0;
 int contador = 0;
 char caracter;
 char mensaje[20];
 void setup() {
   //Configuramos el bluetooth y el serial a 9600
   //bt.begin(9600);
   //bt.begin(300);
   //bt.begin(600);
   //bt.begin(1200);
   //bt.begin(2400);
   //bt.begin(4800);
   bt.begin(9600);    // Arduino UNO
   //bt.begin(14400);
   //bt.begin(19200);
   //bt.begin(28800);
   //bt.begin(38400);
   //bt.begin(57600);  // Arduino Mega
   //bt.begin(115200);
 }
 void loop() {
   while(bt.available()>0){
     caracter = bt.read();
     mensaje[i] = caracter;
     i++;
   }
   if(i > 0){
     bt.print(mensaje);
     bt.print('\n');
     i = 0;
   }
   String str = "Hola mundo";
   bt.print(str);
   bt.write('\n');
   delay(200);
   bt.print(contador);
   bt.write('\n');
   contador++;
   if(contador == 70){ contador = 0; }
   delay(200);
 }
