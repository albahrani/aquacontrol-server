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
package com.github.albahrani.aquacontrol.server.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.restexpress.Request;
import org.restexpress.Response;

import com.github.albahrani.aquacontrol.core.LightEnvironment;
import com.github.albahrani.aquacontrol.core.LightEnvironmentChannel;
import com.github.albahrani.aquacontrol.core.environment.PWMControllerConnector;
import com.github.albahrani.aquacontrol.server.LightServerController;
import com.github.albahrani.aquacontrol.server.json.JSONConfigurationChannel;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.Pin;

import io.netty.handler.codec.http.HttpResponseStatus;

public class ChannelControllerTest {

	@Test
	public void testGetChannels() {
		LightServerController daemon = mock(LightServerController.class);
		LightEnvironment lightEnv = mock(LightEnvironment.class);
		LightEnvironmentChannel channel1 = mock(LightEnvironmentChannel.class);
		when(channel1.id()).thenReturn("Channel1");
		when(channel1.name()).thenReturn("White");
		when(channel1.lastValue()).thenReturn(50.0d);
		when(channel1.pins()).thenReturn(Stream.of(PCA9685Pin.PWM_00));
		LightEnvironmentChannel channel2 = mock(LightEnvironmentChannel.class);
		when(channel2.id()).thenReturn("Channel2");
		when(channel2.name()).thenReturn("Red");
		when(channel2.lastValue()).thenReturn(25.0d);
		when(channel2.pins()).thenReturn(Stream.of(PCA9685Pin.PWM_01, PCA9685Pin.PWM_02));
		when(lightEnv.channels()).thenReturn(Stream.of(channel1, channel2));
		when(daemon.getLightEnvironment()).thenReturn(lightEnv);
		ChannelController controller = new ChannelController(daemon);

		Request request = mock(Request.class);
		Response response = new Response();
		controller.getChannels(request, response);

		Object body = response.getBody();
		assertNotNull(body);
		assertTrue(body instanceof HashMap<?, ?>);
		@SuppressWarnings("unchecked")
		HashMap<String, JSONConfigurationChannel> report = (HashMap<String, JSONConfigurationChannel>) body;
		assertEquals(2, report.size());
		JSONConfigurationChannel c1 = report.get("Channel1");
		assertNotNull(c1);
		assertEquals("White", c1.getName());
		List<String> pins1 = c1.getPins();
		assertNotNull(pins1);
		assertEquals(1, pins1.size());
		assertTrue(pins1.contains(PCA9685Pin.PWM_00.getName()));

		JSONConfigurationChannel c2 = report.get("Channel2");
		assertNotNull(c2);
		assertEquals("Red", c2.getName());
		List<String> pins2 = c2.getPins();
		assertNotNull(pins2);
		assertEquals(2, pins2.size());
		assertTrue(pins2.contains(PCA9685Pin.PWM_01.getName()));
		assertTrue(pins2.contains(PCA9685Pin.PWM_02.getName()));
	}

	@Test
	public void testAddChannel() {
		PWMControllerConnector pwmConn = mock(PWMControllerConnector.class);

		LightEnvironment lightEnv = mock(LightEnvironment.class);
		when(lightEnv.getPwmControllerConnector()).thenReturn(pwmConn);

		LightServerController daemon = mock(LightServerController.class);
		when(daemon.getLightEnvironment()).thenReturn(lightEnv);

		JSONConfigurationChannel jsonChannel = mock(JSONConfigurationChannel.class);
		when(jsonChannel.getName()).thenReturn("White");
		when(jsonChannel.getColor()).thenReturn("#000000");
		List<String> pins = new ArrayList<>();
		pins.add("PWM 1");
		pins.add("PWM 2");
		when(jsonChannel.getPins()).thenReturn(pins);

		Request request = mock(Request.class);
		when(request.getHeader("channelId", "Channel Id is missing.")).thenReturn("white");
		when(request.getBodyAs(JSONConfigurationChannel.class)).thenReturn(jsonChannel);

		Response response = new Response();

		ChannelController controller = new ChannelController(daemon);
		controller.addChannel(request, response);

		assertEquals(HttpResponseStatus.OK, response.getResponseStatus());

		ArgumentCaptor<LightEnvironmentChannel> lightChannel = ArgumentCaptor.forClass(LightEnvironmentChannel.class);
		verify(lightEnv).addChannel(eq("white"), lightChannel.capture());
		assertEquals("White", lightChannel.getValue().name());
		assertEquals("#000000", lightChannel.getValue().color());
		Stream<Pin> lightChannelPins = lightChannel.getValue().pins();
		assertTrue(lightChannelPins.allMatch(p -> {
			return PCA9685Pin.PWM_01.equals(p) || PCA9685Pin.PWM_02.equals(p);
		}));

	}
}
