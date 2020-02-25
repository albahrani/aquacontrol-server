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

import static org.junit.Assert.fail;
import static org.mockito.AdditionalMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

import com.github.albahrani.aquacontrol.logger.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.albahrani.dimmingplan.DimmingPlan;
import com.github.albahrani.dimmingplan.DimmingPlanChannel;

public class LightTaskTest {

	@BeforeClass
	public static void beforeClass() {
		Logger.setActive(false);
	}

	private LightTaskDaemon daemon = mock(LightTaskDaemon.class);
	private LightEnvironment environment = mock(LightEnvironment.class);
	private LightEnvironmentChannel environmentChannel = mock(LightEnvironmentChannel.class);
	private DimmingPlan plan = mock(DimmingPlan.class);
	private DimmingPlanChannel channel = mock(DimmingPlanChannel.class);
	private LightTask lightTask = new LightTask();

	@Before
	public void before() {
		lightTask.setDaemon(daemon);
	}

	@Test
	public void testNotInitialized() {

		try {
			lightTask.executePlanFor(LocalTime.of(0, 0));
			fail("Should throw an exception.");
		} catch (Throwable t) {
			// the exception is wanted here
			verify(daemon).getLightPlan();
			verify(daemon).getLightEnvironment();
		}
	}

	@Test
	public void testRunWithoutPlan() {
		try {
			lightTask.run();
			verify(daemon).getLightPlan();
			verify(daemon).getLightEnvironment();
		} catch (Throwable t) {
			fail("Unexpected error:" + t.getMessage());
		}
	}

	@Test
	public void testPlanPercentagePresent() {

		when(environment.channels()).thenReturn(Stream.of(environmentChannel));
		when(environmentChannel.getId()).thenReturn("0x40");

		Set<String> channelNames = new HashSet<>();
		channelNames.add("0x40");
		when(plan.getChannelNames()).thenReturn(channelNames);
		when(plan.channel("0x40")).thenReturn(channel);
		LocalTime time = LocalTime.of(12, 0);
		when(channel.getPercentage(time)).thenReturn(OptionalDouble.of(100.0d));
		when(daemon.getLightPlan()).thenReturn(plan);
		when(daemon.getLightEnvironment()).thenReturn(environment);

		lightTask.executePlanFor(time);

		verify(plan).channel("0x40");
		verify(daemon).getLightPlan();
		verify(daemon).getLightEnvironment();
		verify(environment).channels();
		verify(environmentChannel).getId();
		verify(environmentChannel).percentage(eq(100.0d, 0.01d));
		verify(channel).getPercentage(time);
	}

	@Test
	public void testPlanPercentageNotPresent() {

		when(environmentChannel.getId()).thenReturn("0x40");
		when(environment.channels()).thenReturn(Stream.of(environmentChannel));
		LocalTime time = LocalTime.of(12, 0);
		when(channel.getPercentage(time)).thenReturn(OptionalDouble.empty());
		when(plan.channel("0x40")).thenReturn(channel);
		when(daemon.getLightEnvironment()).thenReturn(environment);
		when(daemon.getLightPlan()).thenReturn(plan);

		lightTask.executePlanFor(time);

		verify(daemon).getLightPlan();
		verify(daemon).getLightEnvironment();
		verify(environment).channels();
		verify(environmentChannel).getId();
		verify(environmentChannel).percentage(eq(0.0d, 0.01d));
		verify(plan).channel("0x40");
		verify(channel).getPercentage(time);
	}

	@After
	public void after() {
		verifyNoMoreInteractions(daemon, environment, environmentChannel, plan, channel);
	}
}
