#ifndef Motor_h 
#define Motor_h 
//    FILE: Motor.h 
// PURPOSE: Motor Control  library for Arduino 
// HISTORY: See Motor.cpp 

class Motor 
{ 
	public: 
		//void MotorOn(const int &enable, const int &high, const int &low); 
		void MotorOn(unsigned char &flag, unsigned char &motorvalue); 
		void killMotors(bool &flag_f, bool &flag_b, unsigned char &last_com); 
		//void stopMotor(unsigned char &lastcom); 
	 
	protected: 
		/*typedef struct way { 
			int enable; 
			int high; 
			int low; 
		}; 
		const int motor1Pin1 = 5;    // H-bridge leg 1 (pin 2, 1A) 
		const int motor1Pin2 = 3;    // H-bridge leg 2 (pin 7, 2A) 
		const int enablePin1 = 4;    // H-bridge enable pin 
		const int motor2Pin1 = 6;    // H-bridge leg 1 (pin 2, 1A) 
		const int motor2Pin2 = 9;    // H-bridge leg 2 (pin 7, 2A) 
		const int enablePin2 = 8;    // H-bridge enable pin 
		*/ 
}; 

#endif 
