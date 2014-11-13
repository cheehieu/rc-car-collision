//    FILE: ReadSensor.cpp 
// PURPOSE: ReadSensor library for Arduino 
#include "Arduino.h" 
#include "ReadSensor.h" 
#include "RunningMedian.h" 



// Reads Sensor Value, returns running median 
void ReadSensor::SensorRead(int &value_f, int &value_b) 
{ 
	void clear(); 
	int counter = 0; 
	while(counter!=9){ //collect 9 readings from both sensors 
		int x = analogRead(A0); //Read front sensor 
		int y = analogRead(A1); //Read back sensor 
		samples_f.add(x); 
		//Serial.println(x); 
		//delay(70); 
		samples_b.add(y); 
		counter++; 
		delay(10); 
	} 
	value_f = samples_f.getMedian(); //take median of readings from front sensor 
	value_b = samples_b.getMedian(); //take median of readings from back sensor 
	Serial.println(value_f); //print median value from front sensor 
	//Serial.println(value_b); //print median value from back sensor 
	void clear(); 
    counter=0; 
} 

// returns distance condition flag 
void ReadSensor::AnalyzeDanger(int value_f, int value_b, bool &prev_f, bool &prev_b, int &danger_f, int &danger_b, bool &dflag_f, bool &dflag_b, unsigned char &lastcom) 
{ 
	int dis_f = 121; 
	if(lastcom == 0x09) 
	{ 
		dis_f = 93; 
	} 
	else if(lastcom == 0x0a) 
	{ 
		dis_f = 70; 
	} 
	if(value_f>=dis_f){ //median distance value too close on front sensor 
      danger_f++; 
      prev_f = true; 
      if(danger_f>2 && prev_f==true){ 
		danger_f = 0; 
		prev_f = false; 
        //Serial.println("danger_f"); 
		dflag_f=true; 
      } 
    } 
    else if(value_f<dis_f){ //safe median distance value reading on front sensor 
      prev_f = false; 
      danger_f = 0; 
    } 
	if(value_b>=30){ //median distance value too close on back sensor 
      danger_b++; 
      prev_b = true; 
      if(danger_b>2 && prev_b==true){ 
		danger_b = 0; 
		prev_b = false; 
        //Serial.println("danger_b"); 
		dflag_b=true; 
      } 
    } 
    else if(value_b<30){ //safe median distance value reading on back sensor 
      prev_b = false; 
      danger_b = 0; 
    } 
} 

void ReadSensor::DangerLoop(int value_f, int value_b, bool &prevl_f, bool &prevl_b, int &dangerl_f, int &dangerl_b, bool &dflag_f, bool &dflag_b, unsigned char &lastcom) 
{ 
	int dis_f = 121; 
	if(lastcom == 0x09) 
	{ 
		dis_f = 93; 
	} 
	else if(lastcom == 0x0a) 
	{ 
		dis_f = 70; 
	} 
	if(value_f<dis_f){ //safe median distance value reading on front sensor 
      dangerl_f++; 
      prevl_f = true; 
      if(dangerl_f>2 && prevl_f==true){ 
		dangerl_f = 0; 
		prevl_f=false; 
		dflag_f=false; 
		//Serial.println("good_f");		 
      } 
    } 
    else if(value_f>=dis_f){ //median distance value too close on front sensor 
      prevl_f = false; 
      dangerl_f = 0; 
    } 
	if(value_b<30){ //median distance value too close on back sensor 
      dangerl_b++; 
      prevl_b = true; 
      if(dangerl_b>2 && prevl_b==true){ 
		dangerl_b = 0; 
		prevl_b = false; 
		dflag_b=false; 
		//Serial.println("good_b"); 
      } 
    } 
    else if(value_b>=30){ //safe median distance value reading on back sensor 
      prevl_b = false; 
      dangerl_b = 0; 
    } 
} 
 
void ReadSensor::Cruise(int value_f, int value_b, bool &pf, bool &pr, bool &pc, int &cf, int &cr, int &cc, unsigned char &cstatus) 
{ 
	//Serial.println("value of front sensor"); 
	//Serial.println(value_f); 
	if(20<=value_f && value_f<=170){ 
		pf = true; 
		pr = false; 
		pc = false; 
		cf++; 
		cr = 0; 
		cc = 0; 
		if(cf>2 && pf==true){ 
			cstatus = 0x00; 
			Serial.println("cstatus is"); 
			Serial.print(cstatus); 
		} 
	} 
	else if(171<=value_f && value_f<=271){ 
		pf = false; 
		pr = true; 
		pc = false; 
		cf = 0; 
		cr++; 
		cc = 0; 
		if(cr>2 && pr==true){ 
			cstatus = 0x05; 
			Serial.println("cstatus is"); 
			Serial.print(cstatus); 
		} 
	} 
	else if(272<=value_f && value_f<=600){ 
		pf = false; 
		pr = false; 
		pc = true; 
		cf = 0; 
		cr = 0; 
		cc++; 
		if(cc>2 && pc==true){ 
			cstatus = 0x0a; 
			Serial.println("cstatus is"); 
			Serial.println(cstatus); 
		} 
	else { 
		cstatus = 0x0a;	 
		} 
	} 
} 
