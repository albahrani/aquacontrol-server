package com.github.albahrani.aquacontrol.core;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.pmw.tinylog.Logger;

import com.github.albahrani.aquacontrol.core.environment.PWMControllerConnector;
import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.io.gpio.Pin;

public class LightEnvironmentChannel {

	private PWMControllerConnector pwmControllerConnector;

	private String id;
	private String name;
	private String color;
	private Set<Pin> pins = new HashSet<>();
	private Double lastValue;
	private Integer lastDurationValue;

	LightEnvironmentChannel() {

	}

	public static LightEnvironmentChannelBuilder create(String id, PWMControllerConnector pwmControllerConnector) {
		Objects.requireNonNull(pwmControllerConnector);
		return new LightEnvironmentChannelBuilder(id, pwmControllerConnector);
	}

	public void setPwmControllerConnector(PWMControllerConnector pwmControllerConnector) {
		this.pwmControllerConnector = pwmControllerConnector;
	}

	public void addPin(Pin pin) {
		Objects.requireNonNull(pin);
		this.pwmControllerConnector.provisionPwmOutputPin(pin);
		this.pins.add(pin);
	}

	public void setId(String id) {
		Objects.requireNonNull(id);
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setLastValue(Double lastValue) {
		Objects.requireNonNull(lastValue);
		this.lastValue = lastValue;
	}

	public void setLastDurationValue(Integer lastDurationValue) {
		Objects.requireNonNull(lastDurationValue);
		this.lastDurationValue = lastDurationValue;
	}

	public String getId() {
		return this.id;
	}

	public String id() {
		return this.id;
	}

	/**
	 * @return the name
	 */
	public String name() {
		return this.name;
	}

	/**
	 * @return the color
	 */
	public String color() {
		return this.color;
	}

	public Double lastValue() {
		return this.lastValue;
	}

	public Integer lastDurationValue() {
		return this.lastDurationValue;
	}

	/**
	 * @return the pins
	 */
	public Stream<Pin> pins() {
		return this.pins.stream();
	}

	public void percentage(double percentage) {
		if (pins == null) {
			Logger.warn("Channel name {} has no pins defined.", this.name);
			return;
		}

		if (this.lastValue != null) {
			double lastValue = this.lastValue;
			if (Math.abs(lastValue - percentage) < 0.0001) {
				// nothing to do. reduce I2C messages
				return;
			}
		}

		int duration = calculatePwmDuration(percentage);
		if (duration < 0) {
			// just ignore it
			Logger.warn("Invalid pwm duration {} for channel {].", duration, this.name);
			return;
		}

		int lastDuration = -1;
		if (this.lastDurationValue != null) {
			lastDuration = this.lastDurationValue;
		}

		if (lastDuration == duration) {
			// nothing to do. reduce I2C messages
			return;
		}

		this.pwmControllerConnector.setPwmValue(this.pins, duration);
		this.lastValue = percentage;
		this.lastDurationValue = duration;
		return;
	}

	public static int calculatePwmDuration(double channelValue) {
		int retVal;
		double durationDouble = (PCA9685GpioProvider.PWM_STEPS - 1) * (channelValue / 100.0d);

		if (durationDouble < 0.01) {
			// switch it off
			retVal = 0;
		} else if (Math.abs(durationDouble - (PCA9685GpioProvider.PWM_STEPS - 1)) < 0.01) {
			// switch it on
			retVal = (PCA9685GpioProvider.PWM_STEPS - 1);
		} else {
			retVal = safeDoubleToInt(durationDouble);
		}

		return retVal;
	}

	private static int safeDoubleToInt(double d) {
		if (d < Integer.MIN_VALUE || d > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(d + " cannot be cast to int without changing its value.");
		}
		return (int) d;
	}
}
