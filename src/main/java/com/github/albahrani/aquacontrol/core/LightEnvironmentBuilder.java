package com.github.albahrani.aquacontrol.core;

import java.util.ArrayList;
import java.util.List;

import com.github.albahrani.aquacontrol.core.environment.PWMControllerConnector;

public class LightEnvironmentBuilder {

	private List<LightEnvironmentChannel> channels = new ArrayList<>();

	private PWMControllerConnector pwmControllerConnector;

	public LightEnvironmentBuilder(PWMControllerConnector pwmControllerConnector) {
		this.pwmControllerConnector = pwmControllerConnector;
	}

	public LightEnvironmentBuilder withChannel(LightEnvironmentChannel channel) {
		this.channels.add(channel);
		return this;
	}

	public LightEnvironment build() {
		LightEnvironment lightEnvironment = new LightEnvironment();
		lightEnvironment.setPwmControllerConnector(this.pwmControllerConnector);

		this.channels.forEach((channel) -> {
			lightEnvironment.addChannel(channel.id(), channel);
		});

		return lightEnvironment;
	}
}
