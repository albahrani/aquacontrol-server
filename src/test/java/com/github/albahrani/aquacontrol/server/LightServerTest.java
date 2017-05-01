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
package com.github.albahrani.aquacontrol.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;
import org.mockito.InOrder;

import com.github.albahrani.aquacontrol.core.LightEnvironment;
import com.github.albahrani.aquacontrol.core.environment.LightEnvironmentChannelConfiguration;
import com.github.albahrani.aquacontrol.core.environment.LightEnvironmentConfiguration;
import com.github.albahrani.aquacontrol.core.environment.production.PCA9685Connector;
import com.github.albahrani.aquacontrol.server.json.JSONChannel;
import com.github.albahrani.aquacontrol.server.json.JSONPlan;
import com.github.albahrani.aquacontrol.server.json.JSONTimeValuePair;
import com.github.albahrani.dimmingplan.DimmingPlan;
import com.github.albahrani.dimmingplan.DimmingPlanChannel;
import com.pi4j.io.gpio.Pin;

public class LightServerTest {

	@Test
	public void test() {
		JSONPlan plan = new JSONPlan();
		List<JSONChannel> channels = new ArrayList<>();
		JSONChannel channel = new JSONChannel();
		channel.setId("0x20");
		List<JSONTimeValuePair> timetable = new ArrayList<>();
		JSONTimeValuePair p1 = new JSONTimeValuePair();
		p1.setTime(LocalTime.of(6, 0));
		p1.setPerc(0.0d);
		timetable.add(p1);
		JSONTimeValuePair p2 = new JSONTimeValuePair();
		p2.setTime(LocalTime.of(8, 0));
		p2.setPerc(100.0d);
		timetable.add(p2);
		channel.setTimetable(timetable);
		channels.add(channel);
		plan.setChannels(channels);

		DimmingPlan config = LightServer.fromJSON(plan);
		assertNotNull(config);
		assertEquals(1, config.getChannelAmount());
		Set<String> channelNames = config.getChannelNames();
		assertNotNull(channelNames);
		assertTrue(channelNames.contains("0x20"));
		DimmingPlanChannel configChannel = config.channel("0x20");
		assertNotNull(configChannel);

		assertThat(configChannel.getPercentage(LocalTime.of(6, 0))).hasValueCloseTo(0.0d, within(0.001d));
		assertThat(configChannel.getPercentage(LocalTime.of(8, 0))).hasValueCloseTo(100.0d, within(0.001d));
	}

	@Test
	public void testLoadConfigNull() {
		PCA9685Connector connector = mock(PCA9685Connector.class);
		try {
			LightServer.createEnvironmentFromConfiguration(null, connector);
			fail("Expected NullPointerException");
		} catch (NullPointerException e) {
			// ignore
		}
	}

	@Test
	public void testLoadConfigWithChannelConfigNull() {
		PCA9685Connector connector = mock(PCA9685Connector.class);

		LightEnvironmentConfiguration config = mock(LightEnvironmentConfiguration.class);
		when(config.getChannelConfig()).thenReturn(null);
		LightServer.createEnvironmentFromConfiguration(config, connector);
		// expect no exception
	}

	@Test
	public void testLoadConfigWithChannelConfigEmpty() {
		PCA9685Connector connector = mock(PCA9685Connector.class);

		LightEnvironmentConfiguration config = mock(LightEnvironmentConfiguration.class);

		List<LightEnvironmentChannelConfiguration> channelConfig = new ArrayList<>();

		when(config.getChannelConfig()).thenReturn(channelConfig);
		LightServer.createEnvironmentFromConfiguration(config, connector);
		// expect no exception
	}

	@Test
	public void testLoadConfig() {
		PCA9685Connector connector = mock(PCA9685Connector.class);

		LightEnvironmentConfiguration config = mock(LightEnvironmentConfiguration.class);

		LightEnvironmentChannelConfiguration entry = mock(LightEnvironmentChannelConfiguration.class);
		when(entry.getId()).thenReturn("Channel1");
		List<String> pins = new ArrayList<>();
		pins.add("PWM 0");
		when(entry.getPins()).thenReturn(pins);

		List<LightEnvironmentChannelConfiguration> channelConfig = new ArrayList<>();
		channelConfig.add(entry);

		when(config.getChannelConfig()).thenReturn(channelConfig);
		LightEnvironment env = LightServer.createEnvironmentFromConfiguration(config, connector);

		env.channel("Channel1").percentage(100);
		env.channel("Channel1").percentage(50);
		env.channel("Channel1").percentage(0);

		verify(connector).provisionPwmOutputPin(any(Pin.class));
		InOrder inOrder = inOrder(connector);
		inOrder.verify(connector).setPwmValue(anySet(), eq(4095));
		inOrder.verify(connector).setPwmValue(anySet(), eq(2047));
		inOrder.verify(connector).setPwmValue(anySet(), eq(0));
	}

	@Test
	public void testReadLightPlanInputStream() {
		String rn = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(rn);
		sb.append("  \"channels\" : [ {");
		sb.append(rn);
		sb.append("    \"id\" : \"0x20\",");
		sb.append(rn);
		sb.append("    \"timetable\" : [ {");
		sb.append(rn);
		sb.append("      \"time\" : [ 6, 0 ],");
		sb.append(rn);
		sb.append("      \"perc\" : 0.0");
		sb.append(rn);
		sb.append("    }, {");
		sb.append(rn);
		sb.append("      \"time\" : [ 8, 0 ],");
		sb.append(rn);
		sb.append("      \"perc\" : 100.0");
		sb.append(rn);
		sb.append("    } ]");
		sb.append(rn);
		sb.append("  } ]");
		sb.append(rn);
		sb.append("}");
		String testString = sb.toString();

		InputStream stream = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
		Optional<JSONPlan> optionalJsonPlan = LightServer.readLightPlanInputStream(stream);
		
		assertNotNull(optionalJsonPlan);
		assertTrue(optionalJsonPlan.isPresent());
		JSONPlan jsonPlan = optionalJsonPlan.get();
		assertNotNull(jsonPlan);
		List<JSONChannel> channels = jsonPlan.getChannels();
		assertNotNull(channels);
		assertEquals(1, channels.size());
		JSONChannel channel = channels.get(0);
		assertNotNull(channel);
		assertEquals("0x20", channel.getId());
		List<JSONTimeValuePair> timetable = channel.getTimetable();
		assertNotNull(timetable);
		assertEquals(2, timetable.size());
		JSONTimeValuePair tte1 = timetable.get(0);
		assertEquals(LocalTime.of(6, 0), tte1.getTime());
		assertEquals(0.0d, tte1.getPerc(), 0.001d);
		JSONTimeValuePair tte2 = timetable.get(1);
		assertEquals(LocalTime.of(8, 0), tte2.getTime());
		assertEquals(100.0d, tte2.getPerc(), 0.001d);
	}
}
