#ifndef ReadSensor_h 
#define ReadSensor_h 
// 
//    FILE: ReadSensor.h 
// PURPOSE: ReadSensor library for Arduino 
// HISTORY: See ReadSensor.cpp 

#include <C:\Users\Lance\Desktop\arduino-1.0-windows\arduino-1.0\libraries\RunningMedian\RunningMedian.h> 


class ReadSensor 
{ 
	public: 
	RunningMedian samples_f; 
	RunningMedian samples_b; 
	void SensorRead(int &value_f, int &value_b); 
	void AnalyzeDanger(int value_f, int value_b, bool &prev_f, bool &prev_b, int &danger_f, int &danger_b, bool &dflag_f, bool &dflag_b, unsigned char &lastcom); 
	void DangerLoop(int value_f, int value_b, bool &prevl_f, bool &prevl_b, int &dangerl_f, int &dangerl_b, bool &dflag_f, bool &dflag_b, unsigned char &lastcom); 
	void Cruise(int value_f, int value_b, bool &pf, bool &pr, bool &pc, int &cf, int &cr, int &cc, unsigned char &cstatus); 

	 
	protected: 
}; 

#endif
