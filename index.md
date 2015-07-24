---
layout: project
title: rc-car-collision
subtitle: A real-time computer system to prevent head-on traffic collisions (of RC cars).
---

<img src="http://niftyhedgehog.com/rc-car-collision/images/profile_view.jpg">

## Overview
Traffic accidents are one of the leading causes of death among humans. It is predicted that by 2020, traffic accidents will exceed HIV/AIDS as a burden of death and disability. And by 2030, it will become the fifth leading cause of death, period. Accidents occur because of humans' inability to react quickly in situations that require fast responses. Furthermore, a human’s reaction time is crippled by physcial impairments such as drug usage, poor eyesight, sleep deprivation, and inexperience. External factors such as poor road conditions, inclement weather, speeding, and distractions (both from inside the vehicle or from the surrounding environment) also play a significant role. The occurrence of accidents is highly dependant on factors relating to human behavior, sensory perception, decision making, reaction speed, awareness, and alertness.

Our solution to the rising problem of road traffic accidents is to reduce human error by employing real-time computer processes for sensing, perception, and reaction. Avoiding traffic collisions is a life-critical task that depends on real-time systems to operate reliably. This system must meet its deadlines in a predictable manner, but also process sensor data correctly to avoid false positive results. We will build a vehicle that can automatically avoid head-on collisions, maintain a constant distance when following another car/object, and allow manual control override by a remote user through a graphical user interface. 

This project was developed in the spring of 2012 for USC's Real Time Computer Systems (EE-554) course, taught by professor Monte Ung. My team members included Austen Hagio, Lance Sakamoto, and Zachary Slavis.


## Hardware
The Sensing Mobile Android-assisted Real Time (SMART) Car is an RC car retrofitted with a real-time computer system to prevent head-on traffic collisions. This scaled hardware platform consists of RC car, microcontroller, and Android-powered smartphones.

One smart phone is mounted to the front of the car, providing the vehicle with a rich collection of high-performance sensors and essentially, a global communications endpoint. The on-board smart phone runs a mobile application to read data from the various sensors and communicates this information to the controller phone over a Bluetooth connection. The sensors used are a camera for obtaining visual data, an orientation sensor to measure the car's heading, a 3-axis accelerometer to measure the car's acceleration, and a GPS for determining the car's geolocation, speed, and altitude. The mobile application also provides access to the camera's dual-LED flash, which can be used as car headlights in low-light conditions.

<img src="http://niftyhedgehog.com/rc-car-collision/images/EE554ProjectProposal.png">

An Arduino Uno was used for proximity sensing, motor control, and Bluetooth communication. On the front of the vehicle, we mounted a Sharp GP2Y0S02YK0F Long Range Infrared Proximity Sensor, which can measure distances in the range of 20 to 150 cm. On the back of the vehicle, we mounted a Sharp GP2D120XJ00F Short Range Infrared Proximity Sensor, which can measure distances in the range of 4 to 30 cm. For motor control, a L293NE quadruple half H-bridge IC was used to allow bidirectional current flow as well as the disabling of motors to come to a rolling stop.

The Arduino Uno does not contain a built-in transceiver for wireless communication. Instead, an external module was interfaced to its UART serial port, and communication with the TTL serial device occurred across the digital RX/TX pins. This was done using the BlueSMiRF Silver Bluetooth modem to send and receive data streams for motor control and mode configuration from a remote controller smart phone.  

A diagram of the the motors, L293, proximity sensors, Bluetooth module, and Arduino connections is shown in the figure below.

<img src="http://niftyhedgehog.com/rc-car-collision/images/Smart_Car_diagram.png">

<img src="http://niftyhedgehog.com/rc-car-collision/images/aerial_view.jpg">

<img src="http://niftyhedgehog.com/rc-car-collision/images/star_view.jpg">


## Software
This project encompassed analog signal processing, Bluetooth communication, motor control, interrupts & scheduling, and fault-tolerance & recovery. The major software components included: 

* Remote motor control from a smartphone
* Sensor data processing for "collision avoidance" and cruise control"
* Bluetooth communication protocol and data streaming

For motor control, pulse width modulation was used to change speed and direction of the car. The motor control algorithm was abstracted to simplify the interface with a smartphone connected over Bluetooth. The flags 'a' and 'd' correspond to the front and rear motor respectively, while the motor values could range from 1-10 to indicate speed and direction. For the front motor, values less than 5 correspond to turning left while values greater than 5 correspond to turning right. A value of 5 represents the motor at rest.

```
void Motor::MotorOn(unsigned char &flag, unsigned char &motorvalue) 
{ 
	int speed; 
	if (flag == 'a') { 
		if (motorvalue<5) { 
		//LEFT (a) 
			digitalWrite(HBRIDGE_EN, HIGH); 
			speed = (5-motorvalue)*51; 
			analogWrite(MOTOR1PIN1, speed); 
			digitalWrite(MOTOR1PIN2, LOW); 
			digitalWrite(HBRIDGE_EN, LOW); 
		} else if (motorvalue>5) { 
		//RIGHT (d) 
			digitalWrite(HBRIDGE_EN, HIGH); 
			speed = (motorvalue-5)*51; 
			analogWrite(MOTOR1PIN2, speed); 
			digitalWrite(MOTOR1PIN1, LOW); 
			digitalWrite(HBRIDGE_EN, LOW); 
		} 
		else{ 
			digitalWrite(HBRIDGE_EN, LOW);
			digitalWrite(MOTOR1PIN2, LOW);
			digitalWrite(MOTOR1PIN1, LOW);  
		} 
	} else if (flag == 'd') { 
	
	...
	
} 
```

The Arduino Uno microcontroller interprets the proximity data from the IR sensors in every cycle. A [running median](http://arduino.cc/playground/Main/RunningMedian) is used to eliminate distance glitches from the sensor. The most real-time critical aspect of the motor control is the ability of the car to brake in time to avoid objects. For collision avoidance, the motors were applied in the reverse direction when the sensors detected an object a certain distance away, depending on the current speed. In cruise control mode, distance flags are used to signal the car to accelerate, decelerate, or comes to a stop in order to maintain a constant distance from an object in front of it.

By utilizing the Amarino "MeetAndroid" library, we implemented an effective Bluetooth control protocol using flags and callback functions. The [Amarino Toolkit](http://www.amarino-toolkit.net/) contains a set of tools used to connect Android-driven mobile devices with Arduino microcontrollers via Bluetooth. It provides easy access to internal phone events which can be further processed on the Arduino linked to the MeetAndroid library. Essentially, the Arduino registers a callback function to perform on receipt of a unique flag from the smart phone. Within the callback functions, we can then read the rest of the Bluetooth message that contains motor commands or operation mode configuration instructions. From here, our motor control library processes the motor commands and allows the car to move full speed ahead!

Android applications were created as user interfaces for the on-board smartphone and remote controller smartphone. The onboard app displays sensor data from the compass, accelerometer, and GPS, and also records a live camera feed. The controller app receives the video stream and can transmit hard motor commands or configure the system for "collision avoidance" and "cruise control" modes. The Android BluetoothChatService library was utilized to send serial streams through the Bluetooth socket. 

<img src="http://niftyhedgehog.com/rc-car-collision/images/onboard_app.png" width="49%">
<img src="http://niftyhedgehog.com/rc-car-collision/images/controller_app.png" width="50%">


## Demo
<iframe width="560" height="315" src="https://www.youtube.com/embed/4o_OnwHa5dQ" frameborder="0" allowfullscreen></iframe>
