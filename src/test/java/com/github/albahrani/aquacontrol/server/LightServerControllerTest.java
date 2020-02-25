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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.time.LocalTime;
import java.util.Optional;

import com.github.albahrani.aquacontrol.logger.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.albahrani.aquacontrol.core.LightEnvironment;
import com.github.albahrani.aquacontrol.core.LightTask;
import com.github.albahrani.aquacontrol.core.LightTimer;
import com.github.albahrani.aquacontrol.server.json.JSONPlan;
import com.github.albahrani.aquacontrol.server.rest.RESTServer;
import com.github.albahrani.dimmingplan.DimmingPlan;
import com.github.albahrani.dimmingplan.DimmingPlanChannel;
import org.pmw.tinylog.Level;

public class LightServerControllerTest {

	private LightEnvironment environment = mock(LightEnvironment.class);
	private DimmingPlan plan = mock(DimmingPlan.class);
	private DimmingPlanChannel channel = mock(DimmingPlanChannel.class);
	private LightTimer timer = mock(LightTimer.class);
	private RESTServer server = mock(RESTServer.class);
	private JSONPlan jsonPlan = mock(JSONPlan.class);
	private LightPlanStorage lightPlanStorage = mock(LightPlanStorage.class);
	private LightServerController daemon = new LightServerController();


	@BeforeClass
	public static void beforeClass() {
		Logger.setActive(false);
	}

	@Before
	public void before() {
		daemon.setLightEnvironment(environment);
		daemon.setLightPlan(plan);
		daemon.setServer(server);
		daemon.setTimer(timer);
		daemon.setLightPlanStorage(lightPlanStorage);
	}

	@Test
	public void testSetForcedValue() {
		when(plan.channel("0x20")).thenReturn(channel);

		daemon.setForcedValue("0x20", 10.0d);
		verify(plan).channel("0x20");
		verify(channel).pin(10.0d);
	}

	@Test
	public void testClearForcedValue() {
		when(plan.channel("0x20")).thenReturn(channel);

		daemon.clearForcedValue("0x20");
		verify(plan).channel("0x20");
		verify(channel).unpin();
	}

	@Test
	public void testStart() {
		daemon.start();
		verify(server).start();
		verify(timer).start(daemon);
	}

	@Test
	public void testShutdown() {
		daemon.shutdown();

		verify(server).shutdown();
		verify(timer).shutdown();
		verify(environment).shutdown();
	}

	@Test
	public void testPause() {
		daemon.pause();
		verify(timer).stop();
	}
	
	@Test
	public void testResume() {
		daemon.resume();

		verify(timer).start(daemon);

	}

	@Test
	public void testUpdateLightPlan() {
		Optional<File> file = Optional.of(new File("test"));
		when(lightPlanStorage.getLightPlanFile()).thenReturn(file);

		daemon.updateLightPlan(jsonPlan);

		verify(lightPlanStorage).setJsonLightPlan(jsonPlan);
		verify(lightPlanStorage).getLightPlanFile();
		verify(lightPlanStorage).storeLightPlanToFile(file);
	}

	@After
	public void after() {
		verifyNoMoreInteractions(environment, plan, channel, timer, server, jsonPlan, lightPlanStorage);
	}
}
