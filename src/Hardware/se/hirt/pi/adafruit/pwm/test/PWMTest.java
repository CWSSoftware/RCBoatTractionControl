/*
 *  Copyright (C) 2015 Marcus Hirt
 *                     www.hirt.se
 *
 * This software is free:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESSED OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright (C) Marcus Hirt, 2015
 */
package Hardware.se.hirt.pi.adafruit.pwm.test;

import java.io.IOException;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import Hardware.se.hirt.pi.adafruit.pwm.PWMDevice;
import Hardware.se.hirt.pi.adafruit.pwm.PWMDevice.PWMChannel;

/**
 * This example assumes two servos connected to channel 0 and 1, and two H bridges controlling DC engines on channel 2 and 3. 
 * ___DO NOT RUN THIS EXAMPLE WITH SERVOS ON CHANNEL 2 and 3!___ 
 * 
 * @author Marcus Hirt
 */
public class PWMTest {
	// The internetz says 50Hz is the standard PWM frequency for operating RC servos.  
	private final static int SERVO_FREQUENCY = 50;
	// Literature on RC servos says a 1ms pulse is minimum, 1,5ms is centered and 2ms is max. 
	private final static int SERVO_MIN = calculatePulseWidth(1, SERVO_FREQUENCY);
	private final static int SERVO_CENTERED = calculatePulseWidth(1.5, SERVO_FREQUENCY);
	private final static int SERVO_MAX = calculatePulseWidth(2, SERVO_FREQUENCY);

	private final static int MOTOR_MIN = 0;
	private final static int MOTOR_MEDIUM = 2048;
	private final static int MOTOR_MAX = 4095;


	public static void main(String[] args) throws IOException,
			InterruptedException, UnsupportedBusNumberException {
		System.out.println("Creating device...");
		PWMDevice device = new PWMDevice();
		device.setPWMFreqency(SERVO_FREQUENCY);
		PWMChannel servo0 = device.getChannel(0);
		PWMChannel servo1 = device.getChannel(1);
		PWMChannel motor0 = device.getChannel(2);
		PWMChannel motor1 = device.getChannel(3);
		

		System.out.println("Setting start conditions...");
		servo0.setPWM(0, SERVO_CENTERED);
		servo1.setPWM(0, SERVO_CENTERED);
		motor0.setPWM(0, MOTOR_MIN);
		motor1.setPWM(0, MOTOR_MIN);

		System.out.println("Running perpetual loop...");
		while (true) {
			servo0.setPWM(0, SERVO_MIN);
			servo1.setPWM(0, SERVO_MIN);
			motor0.setPWM(0, MOTOR_MEDIUM);
			motor1.setPWM(0, MOTOR_MEDIUM);
			Thread.sleep(500);
			servo0.setPWM(0, SERVO_MAX);
			servo1.setPWM(0, SERVO_MAX);
			motor0.setPWM(0, MOTOR_MAX);
			motor1.setPWM(0, MOTOR_MAX);
			Thread.sleep(500);
			servo0.setPWM(0, SERVO_CENTERED);
			servo1.setPWM(0, SERVO_CENTERED);
			motor0.setPWM(0, MOTOR_MIN);
			motor1.setPWM(0, MOTOR_MIN);
			Thread.sleep(1000);
		}
	}


	private static int calculatePulseWidth(double millis, int frequency) {
		return (int) (Math.round(4096 * millis * frequency/1000));
	}

}
