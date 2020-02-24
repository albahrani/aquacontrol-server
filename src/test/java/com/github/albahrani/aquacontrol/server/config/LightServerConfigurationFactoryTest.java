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
package com.github.albahrani.aquacontrol.server.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;

import com.github.albahrani.aquacontrol.logger.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.albahrani.aquacontrol.core.environment.LightEnvironmentChannelConfiguration;
import com.github.albahrani.aquacontrol.core.environment.LightEnvironmentConfiguration;

public class LightServerConfigurationFactoryTest {

    @BeforeClass
    public static void beforeClass() {
        Logger.setActive(false);
    }

    @Test
    public void testLoadConfigWithInvalidFile() {
        File file = new File("§$%&/invalid;:");

        try {
            LightServerConfigurationFactory.loadConfiguration(Optional.of(file));
            fail("Should throw an InvalidConfigurationException.");
        } catch (InvalidConfigurationException e) {
            // should reach here
			assertNotNull(e);
		} catch (Throwable e) {
			fail("Should throw an InvalidConfigurationException. Has thrown " + e.getClass().getName() + " instead.");
		}
	}

	@Test
	public void testLoadConfigWithReaderException() {
		try {
			LightServerConfigurationFactory.loadConfiguration((Reader) null);
			fail("Should throw an InvalidConfigurationException.");
		} catch (InvalidConfigurationException e) {
			// should reach here
			assertNotNull(e);
		} catch (Throwable e) {
			fail("Should throw an InvalidConfigurationException. Has thrown " + e.getClass().getName() + " instead.");
		}
	}

	@Test
	public void testLoadConfig() {

		String rn = System.getProperty("line.separator");

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(rn);
		sb.append("	 \"coolwhite\": {\"name\":\"CoolWhite\", \"color\":\"#FFFFFF\", \"pins\": [\"PWM_0\"]},");
		sb.append(rn);
		sb.append("	 \"red\": {\"name\":\"Red\", \"color\":\"#FF0000\", \"pins\": [\"PWM_1\"]},");
		sb.append(rn);
		sb.append("	 \"blue\": {\"name\":\"Blue\", \"color\":\"#0000FF\", \"pins\": [\"PWM_2\"]},");
		sb.append(rn);
		sb.append("	 \"warmwhite\": {\"name\":\"WarmWhite\", \"color\":\"#FFEEEE\", \"pins\": [\"PWM_3\"]}");
		sb.append(rn);
		sb.append("}");

		StringReader reader = new StringReader(sb.toString());
		LightEnvironmentConfiguration configuration = null;
		try {
			configuration = LightServerConfigurationFactory.loadConfiguration(reader);
		} catch (InvalidConfigurationException e) {
			fail("Configuration could not be loaded." + e.getMessage());
		}
		assertNotNull(configuration);
		List<LightEnvironmentChannelConfiguration> channelConfig = configuration.getChannelConfig();
		assertNotNull(channelConfig);
		int numberOfChannels = channelConfig.size();
		assertEquals(4, numberOfChannels);
		LightEnvironmentChannelConfiguration channelCoolWhite = channelConfig.get(0);
		verifyChannelDefinition(channelCoolWhite, "coolwhite", "CoolWhite", "#FFFFFF", new String[] { "PWM_0" });

		LightEnvironmentChannelConfiguration channelRed = channelConfig.get(1);
		verifyChannelDefinition(channelRed, "red", "Red", "#FF0000", new String[] { "PWM_1" });

		LightEnvironmentChannelConfiguration channelBlue = channelConfig.get(2);
		verifyChannelDefinition(channelBlue, "blue", "Blue", "#0000FF", new String[] { "PWM_2" });

		LightEnvironmentChannelConfiguration channelWarmWhite = channelConfig.get(3);
		verifyChannelDefinition(channelWarmWhite, "warmwhite", "WarmWhite", "#FFEEEE", new String[] { "PWM_3" });
	}

	private static void verifyChannelDefinition(LightEnvironmentChannelConfiguration channelDefintion, String channelId, String channelName, String color,
			String[] pinNames) {
		assertNotNull(channelDefintion);
		assertEquals(channelId, channelDefintion.getId());
		assertEquals(channelName, channelDefintion.getName());
		assertEquals(color, channelDefintion.getColor());
		List<String> pins = channelDefintion.getPins();
		assertNotNull(pins);
		assertEquals(pinNames.length, pins.size());
		for (int i = 0; i < pinNames.length; i++) {
			String pin = pins.get(i);
			assertEquals(pinNames[i], pin);
		}
	}

}
