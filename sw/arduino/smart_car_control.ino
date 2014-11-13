#include <MeetAndroid.h> 
#include <RunningMedian.h> 
#include <ReadSensor.h> 
#include <Motor.h> 

MeetAndroid meetAndroid; 
Motor motor; 
byte initFlag = 0x64, initCom1 = 0x07, initCom2 = 0x03, initCom3 = 0x05; 
byte lastcom = 0x05; 
byte cspeed = 0x07; 
byte cstop = 0x08; 
byte lastmotor = 0x05; 
byte forward = 0x64; 
bool stopped = true; 
bool tflag = true; 
bool fflag = false; 
byte mode = 0x01; 
int mode3_flag = 0; 


// Sensor Setup 
  int dangercounter_f = 0; 
  int dangercounter_b = 0; 
  int dangercounterl_f = 0; 
  int dangercounterl_b = 0; 
  int sensorvalue_f = 0; 
  int sensorvalue_b = 0; 
  bool previouscounter_f = false; 
  bool previouscounter_b = false; 
  bool previouscounterl_f = false; 
  bool previouscounterl_b = false; 
  bool dangerflag_f = false; 
  bool dangerflag_b = false; 
  
  bool p_f = false; 
  bool p_r = false; 
  bool p_c = false; 
  int c_f = 0; 
  int c_r = 0; 
  int c_c = 0; 
  byte c_status = 0x07; 
  
  ReadSensor readsensor; 

void setup() 
{ 
    Serial.begin(115200); 
    pinMode(13, OUTPUT);  //onboard LED 
    meetAndroid.registerFunction(motorCommand, 'a'); 
    meetAndroid.registerFunction(motorCommand, 'd'); 
    meetAndroid.registerFunction(modeConfig, 'm'); 
    motor.MotorOn(initFlag, initCom1); 
    motor.MotorOn(initFlag, initCom2); 
    motor.MotorOn(initFlag, initCom3); 
} 

void loop() 
{ 
  switch(mode) 
  { 
      case 0x01:  //Mode 1: Normal Operation 
      meetAndroid.receive(); 
      break; 
      
      case 0x02:  //Mode 2: Head-on collision avoidance 
      // Read proximity sensors, specify minimum distance, and calculate difference from threshold 
      Serial.print(lastcom, HEX); 
      readsensor.SensorRead(sensorvalue_f, sensorvalue_b); 
      readsensor.AnalyzeDanger(sensorvalue_f, sensorvalue_b, previouscounter_f, previouscounter_b, dangercounter_f, dangercounter_b, dangerflag_f, dangerflag_b, lastcom); 
      if (dangerflag_f || dangerflag_b == true) 
      { 
        digitalWrite(13, HIGH); 
        // PID control loops to maintain distance and heading 
        // Motor control from Android smartphone over Bluetooth 
        motor.killMotors(dangerflag_f, dangerflag_b, lastcom); 
        Serial.println("disable motors!"); 
        while(dangerflag_f || dangerflag_b == true) 
        { 
          readsensor.SensorRead(sensorvalue_f, sensorvalue_b); 
          readsensor.DangerLoop(sensorvalue_f, sensorvalue_b, previouscounterl_f, previouscounterl_b, dangercounterl_f, dangercounterl_b, dangerflag_f, dangerflag_b, lastcom); 
        } 
        lastcom = 0x05; 
      } 
      else 
      { 
        digitalWrite(13, LOW); 
        meetAndroid.receive(); 
      } 
      break; 
      
      case 0x03:  //Mode 3: Cruise control 
      readsensor.SensorRead(sensorvalue_f, sensorvalue_b); 
      readsensor.Cruise(sensorvalue_f, sensorvalue_b, p_f, p_r, p_c, c_f, c_r, c_c, c_status); 
      //Serial.print("Status is  = "); 
      //Serial.println(c_status, HEX); 
      //Serial.print("Last motor speed is = "); 
      //Serial.println(lastmotor, HEX); 
      if (c_status == 0x00) { //too far 
        digitalWrite(13, LOW); 
        if (lastmotor<10) { 
          lastmotor++; 
        } 
        stopped = true; 
        motor.MotorOn(forward, cspeed);  
      } 
      else if (c_status == 0x05) { // ok 
        digitalWrite(13, LOW); 
        if (lastmotor>5) { 
          lastmotor--;  
        }  
        stopped = true; 
        motor.MotorOn(forward, cspeed); 
      } 
      else if (c_status == 0x0A) { //too close 
        digitalWrite(13, HIGH); 
        if (stopped) { 
          motor.killMotors(tflag, fflag, cstop); 
          delay(5000); 
        } 
        stopped = false; 
        lastmotor = 0x05; 
      } 
      else { 
          if (stopped) { 
            motor.killMotors(tflag, fflag, cstop); 
            delay(5000); 
          }  
      } 
      Serial.println("stopped flag is"); 
      Serial.println(stopped); 
      mode3_flag = 1; 
      meetAndroid.receive(); 
      break; 
      
    default: 
      Serial.println("State = DEFAULT"); 
      meetAndroid.receive(); 
  } 
} 

void motorCommand(byte flag, byte numOfValues) 
{ 
  byte command = meetAndroid.getInt(); 
  if (mode3_flag == 0) 
  { 
    motor.MotorOn(flag, command); 
    //Serial.print("Flag = "); 
    //Serial.println(flag, HEX); 
    //Serial.print("Command = "); 
    //Serial.println(command, HEX); 
  } 
  
  lastcom = command; 
} 

void modeConfig(byte flag, byte numOfValues) 
{ 
  mode = meetAndroid.getInt(); 
  if (mode3_flag == 1) 
  { 
    mode3_flag = 0; 
  } 
} 
