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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.time.LocalTime;

import org.junit.Test;

import com.github.albahrani.aquacontrol.core.LightEnvironment;
import com.github.albahrani.aquacontrol.core.LightTask;
import com.github.albahrani.aquacontrol.core.LightTimer;
import com.github.albahrani.aquacontrol.server.LightServer;
import com.github.albahrani.aquacontrol.server.LightServerArgs;
import com.github.albahrani.aquacontrol.server.LightServerController;
import com.github.albahrani.aquacontrol.server.json.JSONPlan;
import com.github.albahrani.aquacontrol.server.rest.RESTServer;
import com.github.albahrani.dimmingplan.DimmingPlan;
import com.github.albahrani.dimmingplan.DimmingPlanChannel;

public class LightServerControllerTest {

	@Test
	public void testNoErrorOnEmptyPlanExecution() {

		LightServerController daemon = new LightServerController();
		LightEnvironment environment = mock(LightEnvironment.class);
		daemon.setLightEnvironment(environment);
		DimmingPlan plan = mock(DimmingPlan.class);
		JSONPlan jsonPlan = mock(JSONPlan.class);
		daemon.setLightPlan(plan, jsonPlan);

		try {
			LightTask lightTask = new LightTask();
			lightTask.setDaemon(daemon);

			lightTask.executePlanFor(LocalTime.of(0, 0));
		} catch (Throwable t) {
			fail(t.getMessage());
		}
	}

	@Test
	public void testNotRunningOnRaspi() {
		boolean runningOnRaspberry = LightServer.isRunningOnRaspberry();
		// TODO mock the SystemInfo class
		assertEquals(false, runningOnRaspberry);
	}

	@Test
	public void testArguments() {

		String[] args = new String[] { "-c", "C:/temp/config.json", "-p", "C:/temp/plan.json" };

		LightServerArgs parseArgs = LightServer.parseArgs(args);
		assertNotNull(parseArgs);
		assertEquals(new File("C:/temp/config.json"), parseArgs.getConfigFile());
		assertEquals(new File("C:/temp/plan.json"), parseArgs.getLightPlanFile());
	}

	@Test
	public void testArgumentsOneMissing() {

		String[] args = new String[] { "-c", "C:/temp/config.json" };

		LightServerArgs parseArgs = LightServer.parseArgs(args);
		assertNull(parseArgs);
	}

	@Test
	public void testArgumentsWrongParameter() {

		String[] args = new String[] { "-c", "C:/temp/config.json", "-p", "C:/temp/plan.json", "-x", "Wrong" };

		LightServerArgs parseArgs = LightServer.parseArgs(args);
		assertNull(parseArgs);
	}

	@Test
	public void testArgumentsHelpParameter() {

		String[] args = new String[] { "-h" };

		LightServerArgs parseArgs = LightServer.parseArgs(args);
		assertNotNull(parseArgs);
	}

	@Test
	public void testSetForcedValue() {
		DimmingPlan plan = mock(DimmingPlan.class);
		DimmingPlanChannel channel = mock(DimmingPlanChannel.class);
		when(plan.channel("0x20")).thenReturn(channel);

		JSONPlan jsonPlan = mock(JSONPlan.class);

		LightServerController daemon = new LightServerController();
		daemon.setLightPlan(plan, jsonPlan);
		daemon.setForcedValue("0x20", 10.0d);
		verify(plan).channel("0x20");
		verifyNoMoreInteractions(plan);
		verify(channel).pin(10.0d);
		verifyNoMoreInteractions(channel);
		verifyZeroInteractions(jsonPlan);
	}

	@Test
	public void testClearForcedValue() {
		DimmingPlan plan = mock(DimmingPlan.class);
		DimmingPlanChannel channel = mock(DimmingPlanChannel.class);
		when(plan.channel("0x20")).thenReturn(channel);

		JSONPlan jsonPlan = mock(JSONPlan.class);

		LightServerController daemon = new LightServerController();
		daemon.setLightPlan(plan, jsonPlan);
		daemon.clearForcedValue("0x20");
		verify(plan).channel("0x20");
		verifyNoMoreInteractions(plan);
		verify(channel).unpin();
		verifyNoMoreInteractions(channel);
		verifyZeroInteractions(jsonPlan);
	}

	@Test
	public void testStart() {
		LightTimer timer = mock(LightTimer.class);
		RESTServer server = mock(RESTServer.class);

		LightServerController daemon = new LightServerController();
		daemon.setServer(server);
		daemon.setTimer(timer);
		daemon.start();

		verify(server).start();
		verifyNoMoreInteractions(server);
		verify(timer).start(daemon);
		verifyNoMoreInteractions(timer);
	}

	@Test
	public void testShutdown() {
		LightTimer timer = mock(LightTimer.class);
		RESTServer server = mock(RESTServer.class);
		LightEnvironment env = mock(LightEnvironment.class);

		LightServerController daemon = new LightServerController();
		daemon.setServer(server);
		daemon.setTimer(timer);
		daemon.setLightEnvironment(env);
		daemon.shutdown();

		verify(server).shutdown();
		verifyNoMoreInteractions(server);
		verify(timer).shutdown();
		verifyNoMoreInteractions(timer);
		verify(env).shutdown();
		verifyNoMoreInteractions(env);
	}

	@Test
	public void testResume() {
		LightTimer timer = mock(LightTimer.class);
		RESTServer server = mock(RESTServer.class);

		LightServerController daemon = new LightServerController();
		daemon.setServer(server);
		daemon.setTimer(timer);
		daemon.resume();

		verifyZeroInteractions(server);
		verify(timer).start(daemon);
		verifyNoMoreInteractions(timer);
	}
}
