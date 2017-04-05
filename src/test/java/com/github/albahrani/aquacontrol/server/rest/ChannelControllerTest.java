package com.github.albahrani.aquacontrol.server.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;
import org.restexpress.Request;
import org.restexpress.Response;

import com.github.albahrani.aquacontrol.core.LightEnvironment;
import com.github.albahrani.aquacontrol.core.LightEnvironmentChannel;
import com.github.albahrani.aquacontrol.server.LightServerController;
import com.github.albahrani.aquacontrol.server.json.JSONConfigurationChannel;
import com.pi4j.gpio.extension.pca.PCA9685Pin;

public class ChannelControllerTest {

	@Test
	public void test() {
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

}
