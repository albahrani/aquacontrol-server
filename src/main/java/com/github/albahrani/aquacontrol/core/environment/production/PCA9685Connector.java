/**
 * Copyright © 2017 albahrani (https://github.com/albahrani)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.albahrani.aquacontrol.core.environment.production;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;

import org.pmw.tinylog.Logger;

import com.github.albahrani.aquacontrol.core.environment.PWMControllerConnector;
import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class PCA9685Connector implements PWMControllerConnector {

	private int address = 0x40;
	private PCA9685GpioProvider pca9685;
	private GpioController gpio;

	public PCA9685Connector() {
		try {
			// This would theoretically lead into a resolution of 2,44
			// microseconds per step:
			// 12bit PWM = 4096 Steps
			// MeanWell LDD 1000-H PWM frequency = 100Hz - 1kHz
			//
			// T = 1 / f
			// with f = 100Hz
			// T = 0.001s
			//
			// res = T / steps
			// with T = 0.001s and steps = 4096
			// res = 0.00000244s
			double frequency = 100.0d;
			// Correction factor: actualFreq / targetFreq
			// We don't know it actually so we just take 1.00
			double frequencyCorrectionFactor = 1.00d;

			// Create custom PCA9685 GPIO provider
			I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
			pca9685 = new PCA9685GpioProvider(bus, this.address, BigDecimal.valueOf(frequency), BigDecimal.valueOf(frequencyCorrectionFactor));

			// Reset outputs
			pca9685.reset();

			gpio = GpioFactory.getInstance();
		} catch (IOException | UnsupportedBusNumberException e) {
			Logger.error(e, "Error while initializing PCA9685Connector.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.albahrani.aquacontrol.core.environment.PWMControllerConnector#
	 * setPwmValue(java.util.Set, int)
	 */
	@Override
	public void setPwmValue(Set<Pin> pinList, int duration) {
		if (duration == 0) {
			// switch it off
			pinList.forEach(pca9685::setAlwaysOff);
		} else if (duration == (PCA9685GpioProvider.PWM_STEPS - 1)) {
			// switch it on
			pinList.forEach(pca9685::setAlwaysOn);
		} else {
			pinList.forEach(pin -> this.pca9685.setPwm(pin, 0, duration));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.albahrani.aquacontrol.lightdaemon.environment.PWMControllerConnector#
	 * shutdown()
	 */
	@Override
	public void shutdown() {
		this.pca9685.shutdown();
		gpio.shutdown();
		try {
			I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
			bus.close();
		} catch (IOException | UnsupportedBusNumberException e) {
			Logger.error(e, "Error while shutting down PCA9685Connector.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.albahrani.aquacontrol.lightdaemon.environment.PWMControllerConnector#
	 * provisionPwmOutputPin(com.pi4j.io.gpio.Pin)
	 */
	@Override
	public void provisionPwmOutputPin(Pin pin) {
		gpio.provisionPwmOutputPin(pca9685, pin);
	}
	
	@Override
	public void unprovisionPwmOutputPin(Pin pin) {
		GpioPin provisionedPin = gpio.getProvisionedPin(pin);
		if(provisionedPin != null) {
			gpio.unprovisionPin(provisionedPin);
		}
	}
}
