//    FILE: Motor.cpp 
// PURPOSE: Motor Control library for Arduino 
#include "Arduino.h" 
#include "Motor.h" 

void Motor::MotorOn(unsigned char &flag, unsigned char &motorvalue) 
{ 
	int speed; 
	if (flag == 'a') { 
		if (motorvalue<5) { 
		//LEFT (a) 
			digitalWrite(4, HIGH); 
			//digitalWrite(5, HIGH); 
			speed = (5-motorvalue)*51; 
			analogWrite(5, speed); 
			digitalWrite(3, LOW); 
						 
			//delay(200); 
			digitalWrite(4, LOW); 
		} else if (motorvalue>5) { 
		//RIGHT (d) 
			digitalWrite(4, HIGH); 
			//digitalWrite(3, HIGH); 
			speed = (motorvalue-5)*51; 
			analogWrite(3, speed); 
			digitalWrite(5, LOW); 
			 
			//delay(200); 
			digitalWrite(4, LOW); 
		} 
		else{ 
			digitalWrite(3, LOW); 
			digitalWrite(5, LOW); 
			digitalWrite(4, LOW); 
		} 
	} else if (flag == 'd') { 
		if (motorvalue>5) {	 
		//FORWARD (w) 
			digitalWrite(8, HIGH); 
			//digitalWrite(9, HIGH); 
			speed = (motorvalue-5)*51; 
			analogWrite(9, speed); 
			digitalWrite(6, LOW);		 
			 
			//delay(200); 
			digitalWrite(8, LOW); 
		} else if (motorvalue<5) {	 
		//BACKWARD (s) 
			digitalWrite(8, HIGH); 
			//digitalWrite(6, HIGH); 
			speed = (5-motorvalue)*51; 
			analogWrite(6, speed); 
			digitalWrite(9, LOW); 
			 
		//	delay(200); 
			digitalWrite(8, LOW); 
		} else{ 
			digitalWrite(6, LOW); 
			digitalWrite(9, LOW); 
			digitalWrite(8, LOW); 
		} 
	} else { 
		digitalWrite(4, LOW); 
		digitalWrite(8, LOW); 
	} 
} 

void Motor::killMotors(bool &flag_f, bool &flag_b, unsigned char &lastcom) 
{ 
	int delay_t; 
	if (flag_f) { 
		digitalWrite(8, HIGH); 
		digitalWrite(6, HIGH); 
		digitalWrite(9, LOW); 
		if (lastcom<5) { 
			delay_t = (5-lastcom); 
			delay(delay_t*125); 
		} else if (lastcom>5) { 
			delay_t = (lastcom-5); 
			delay(delay_t*125); 
		} 
		//Serial.print("delay for "); 
		//Serial.println(delay_t, HEX); 
		//delay(100); 
			 
		digitalWrite(3, LOW); 
		digitalWrite(5, LOW); 
		digitalWrite(4, LOW); 
		digitalWrite(6, LOW); 
		digitalWrite(8, LOW); 
		digitalWrite(9, LOW); 
		 
	} else if (flag_b) { 
		digitalWrite(8, HIGH); 
		digitalWrite(9, HIGH); 
		digitalWrite(6, LOW); 
		if (lastcom<5) { 
			delay_t = (5-lastcom); 
			delay(delay_t*125); 
		} else if (lastcom>5) { 
			delay_t = (lastcom-5); 
			delay(delay_t*125); 
		} 
		//delay(100); 
	 
		digitalWrite(3, LOW); 
		digitalWrite(5, LOW); 
		digitalWrite(4, LOW); 
		digitalWrite(6, LOW); 
		digitalWrite(8, LOW); 
		digitalWrite(9, LOW); 
	} 
	else { 
		digitalWrite(3, LOW); 
		digitalWrite(5, LOW); 
		digitalWrite(4, LOW); 
		digitalWrite(6, LOW); 
		digitalWrite(8, LOW); 
		digitalWrite(9, LOW); 
	} 
} 
/* 
void stopMotor(unsigned char &lastcom); 
{ 
	int delay_t; 
	if (lastcom<5) { 
			delay_t = (5-lastcommand); 
			delay(delay_t*50); 
	} else if (lastcom>5) { 
			speed = (lastcom-5)*51; 
			delay(delay_t*50); 
	} 
} 
