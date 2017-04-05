/**
 * Copyright © 2015 albahrani (https://github.com/albahrani)
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
package com.github.albahrani.aquacontrol.server.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.albahrani.aquacontrol.core.environment.LightEnvironmentChannelConfiguration;
import com.github.albahrani.aquacontrol.core.environment.LightEnvironmentConfiguration;
import com.github.albahrani.aquacontrol.server.json.JSONConfigurationChannel;

public class LightServerConfigurationFactory {

	public static LightEnvironmentConfiguration loadConfiguration(Reader reader) throws InvalidConfigurationException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, JSONConfigurationChannel> jsonConfiguration = null;
		try {
			TypeReference<Map<String, JSONConfigurationChannel>> mapType = new TypeReference<Map<String, JSONConfigurationChannel>>() {
				// intentionally left blank
			};
			jsonConfiguration = mapper.readValue(reader, mapType);
		} catch (IOException e) {
			throw new InvalidConfigurationException(e);
		}

		LightEnvironmentConfiguration configuration = fromJson(jsonConfiguration);
		return configuration;
	}

	private static LightEnvironmentConfiguration fromJson(Map<String, JSONConfigurationChannel> orgChannelConfig) {
		LightEnvironmentConfiguration retval = new LightEnvironmentConfiguration();
		List<LightEnvironmentChannelConfiguration> channelConfigs = new ArrayList<>();
		orgChannelConfig.forEach((channelId, orgChannelDefinition) -> {
			LightEnvironmentChannelConfiguration channelConfig = new LightEnvironmentChannelConfiguration();
			channelConfig.setId(channelId);
			channelConfig.setName(orgChannelDefinition.getName());
			channelConfig.setColor(orgChannelDefinition.getColor());
			List<String> pins = new ArrayList<>(orgChannelDefinition.getPins());
			channelConfig.setPins(pins);
			channelConfigs.add(channelConfig);
		});

		retval.setChannelConfig(channelConfigs);
		return retval;

	}

	public static LightEnvironmentConfiguration loadConfiguration(File file) throws InvalidConfigurationException {
		LightEnvironmentConfiguration configuration = null;
		try (FileReader reader = new FileReader(file)) {
			configuration = loadConfiguration(reader);
		} catch (IOException e) {
			throw new InvalidConfigurationException(e);
		}

		return configuration;
	}
}
