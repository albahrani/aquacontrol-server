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
package com.github.albahrani.aquacontrol.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.albahrani.aquacontrol.core.environment.PWMControllerConnector;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.Pin;

public class LightEnvironment {

	private Map<String, LightEnvironmentChannel> channels = new HashMap<>();

	private PWMControllerConnector pwmControllerConnector;

	LightEnvironment() {

	}

	void setPwmControllerConnector(PWMControllerConnector pwmControllerConnector) {
		this.pwmControllerConnector = pwmControllerConnector;
	}

	public PWMControllerConnector getPwmControllerConnector() {
		return this.pwmControllerConnector;
	}

	public void addChannel(String channelId, LightEnvironmentChannel channel) {
		if(this.channels.containsKey(channelId)){
			return;
		}
		this.channels.put(channelId, channel);
	}
	
	public void updateChannel(String channelId, LightEnvironmentChannel channel) {
		if(!this.channels.containsKey(channelId)){
			return;
		}
		List<Pin> newPins = channel.pins().collect(Collectors.toList());
		LightEnvironmentChannel oldChannel = this.channels.put(channelId, channel);		
		oldChannel.pins().filter(pold -> {
			return !newPins.contains(pold);
		}).forEach(pold -> {
			this.pwmControllerConnector.unprovisionPwmOutputPin(pold);
		});;
	}

	public void removeChannel(String channelId) {
		this.channels.remove(channelId);
	}
	
	public static LightEnvironmentBuilder create(PWMControllerConnector pwmControllerConnector) {
		Objects.requireNonNull(pwmControllerConnector);
		return new LightEnvironmentBuilder(pwmControllerConnector);
	}

	public LightEnvironmentChannel channel(String channelId) {
		Objects.requireNonNull(channelId);
		return this.channels.get(channelId);
	}

	public void shutdown() {
		this.pwmControllerConnector.shutdown();
	}

	public Stream<LightEnvironmentChannel> channels() {
		return this.channels.values().stream();
	}

}
