package com.github.albahrani.aquacontrol.core;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.github.albahrani.aquacontrol.core.environment.PWMControllerConnector;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.Pin;

public class LightEnvironmentChannelBuilder {
	private PWMControllerConnector pwmControllerConnector;
	private String id;
	private String name;
	private String color;
	private Set<String> pinNames = new HashSet<>();

	public LightEnvironmentChannelBuilder(String id, PWMControllerConnector pwmControllerConnector) {
		this.id = id;
		this.pwmControllerConnector = pwmControllerConnector;
	}

	public LightEnvironmentChannelBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public LightEnvironmentChannelBuilder withColor(String color) {
		this.color = color;
		return this;
	}

	public LightEnvironmentChannelBuilder usePin(String pinName) {
		this.pinNames.add(pinName);
		return this;
	}

	public LightEnvironmentChannel build() {
		LightEnvironmentChannel channel = new LightEnvironmentChannel();
		channel.setPwmControllerConnector(pwmControllerConnector);
		channel.setId(id);
		channel.setName(name);
		channel.setColor(color);

		this.pinNames.stream().map(LightEnvironmentChannelBuilder::findPin).forEach((optionalPin) -> {
			optionalPin.ifPresent((pin) -> channel.addPin(pin));
		});
		return channel;
	}

	private static Optional<Pin> findPin(String pinName) {
		Objects.requireNonNull(pinName);
		Pin[] allPins = PCA9685Pin.ALL;
		return Stream.of(allPins).filter((pin) -> pin.getName().equals(pinName)).findFirst();
	}
}
