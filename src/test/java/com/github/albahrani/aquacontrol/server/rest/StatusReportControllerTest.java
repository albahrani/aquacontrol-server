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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.github.albahrani.aquacontrol.logger.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restexpress.Request;
import org.restexpress.Response;

import com.github.albahrani.aquacontrol.core.LightEnvironment;
import com.github.albahrani.aquacontrol.core.LightEnvironmentChannel;
import com.github.albahrani.aquacontrol.server.LightServerController;
import com.github.albahrani.aquacontrol.server.json.JSONChannelState;
import com.github.albahrani.aquacontrol.server.json.JSONStatusReport;

public class StatusReportControllerTest {

    @BeforeClass
    public static void beforeClass() {
        Logger.setActive(false);
    }

    @Test
    public void testUptime0ms() {
        String uptime = StatusReportController.formatUptime(0);
        assertEquals("0 min 0 sec", uptime);
    }

    @Test
    public void testUptime500ms() {
        String uptime = StatusReportController.formatUptime(500);
		assertEquals("0 min 0 sec", uptime);
	}

	@Test
	public void testUptime1sec0ms() {
		String uptime = StatusReportController.formatUptime(1000);
		assertEquals("0 min 1 sec", uptime);
	}

	@Test
	public void testUptime2sec0ms() {
		String uptime = StatusReportController.formatUptime(2000);
		assertEquals("0 min 2 sec", uptime);
	}

	@Test
	public void testUptime1min0sec() {
		String uptime = StatusReportController.formatUptime(1000 * 60);
		assertEquals("1 min 0 sec", uptime);
	}

	@Test
	public void testUptime2min0sec() {
		String uptime = StatusReportController.formatUptime(2 * 1000 * 60);
		assertEquals("2 min 0 sec", uptime);
	}

	@Test
	public void testUptime1h0min() {
		String uptime = StatusReportController.formatUptime(1000 * 60 * 60);
		assertEquals("1 hour 0 min 0 sec", uptime);
	}

	@Test
	public void testUptime2h0min() {
		String uptime = StatusReportController.formatUptime(2 * 1000 * 60 * 60);
		assertEquals("2 hours 0 min 0 sec", uptime);
	}

	@Test
	public void testUptime1day0h() {
		String uptime = StatusReportController.formatUptime(1000 * 60 * 60 * 24);
		assertEquals("1 day 0 hours 0 min 0 sec", uptime);
	}

	@Test
	public void testUptime2day0h() {
		mock(LightServerController.class);
		String uptime = StatusReportController.formatUptime(2 * 1000 * 60 * 60 * 24);
		assertEquals("2 days 0 hours 0 min 0 sec", uptime);
	}

	@Test
	public void testGetStatus() {
		LightServerController daemon = mock(LightServerController.class);
		LightEnvironment lightEnv = mock(LightEnvironment.class);
		LightEnvironmentChannel channel1 = mock(LightEnvironmentChannel.class);
		when(channel1.id()).thenReturn("Channel1");
		when(channel1.name()).thenReturn("White");
		when(channel1.lastValue()).thenReturn(50.0d);
		LightEnvironmentChannel channel2 = mock(LightEnvironmentChannel.class);
		when(channel2.id()).thenReturn("Channel2");
		when(channel2.name()).thenReturn("Red");
		when(channel2.lastValue()).thenReturn(25.0d);
		when(lightEnv.channels()).thenReturn(Stream.of(channel1, channel2)).thenReturn(Stream.of(channel1, channel2));
		when(daemon.getLightEnvironment()).thenReturn(lightEnv);
		StatusReportController controller = new StatusReportController(daemon);

		Request request = mock(Request.class);
		Response response = new Response();

		controller.getStatus(request, response);
		Object body = response.getBody();
		assertNotNull(body);
		assertTrue(body instanceof JSONStatusReport);
		JSONStatusReport report = (JSONStatusReport) body;

		Runtime runtime = Runtime.getRuntime();
		assertEquals(String.format("%d Core(s)", runtime.availableProcessors()), report.getAvailableProcessors());
		assertEquals(String.format("%.2f MB", (runtime.freeMemory() / 1048576f)), report.getFreeMemory());
		assertEquals(String.format("%.2f MB", (runtime.totalMemory() / 1048576f)), report.getTotalMemory());
		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		long millis = runtimeBean.getUptime();
		assertEquals(StatusReportController.formatUptime(millis), report.getUptime());
		Set<String> channelIds = report.getChannelIds();
		assertNotNull(channelIds);
		assertEquals(2, channelIds.size());
		assertTrue(channelIds.contains("Channel1"));
		assertTrue(channelIds.contains("Channel2"));

		Map<String, JSONChannelState> lightEnvironment = report.getLightEnvironment();
		assertNotNull(lightEnvironment);
		assertEquals(2, lightEnvironment.size());
		JSONChannelState channel1State = lightEnvironment.get("Channel1");
		assertNotNull(channel1State);
		assertEquals("White", channel1State.getName());
		Double channel1Value = channel1State.getValue();
		assertNotNull(channel1Value);
		assertEquals(50.0d, channel1Value, 0.0001d);

		JSONChannelState channel2State = lightEnvironment.get("Channel2");
		assertNotNull(channel2State);
		assertEquals("Red", channel2State.getName());
		Double channel2Value = channel2State.getValue();
		assertNotNull(channel2Value);
		assertEquals(25.0d, channel2Value, 0.0001d);
	}
}
