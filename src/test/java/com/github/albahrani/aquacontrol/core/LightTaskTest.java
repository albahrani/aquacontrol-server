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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.OptionalDouble;
import java.util.Set;

import org.junit.Test;

import com.github.albahrani.dimmingplan.DimmingPlan;
import com.github.albahrani.dimmingplan.DimmingPlanChannel;

public class LightTaskTest {

	@Test
	public void testNotInitialized() {

		try {
			LightTask lightTask = new LightTask();
			lightTask.executePlanFor(LocalTime.of(0, 0));
			fail("Should throw an exception.");
		} catch (@SuppressWarnings("unused") Throwable t) {
			// the exception is wanted here
		}
	}

	@Test
	public void testRunWithoutPlan() {
		try {
			LightTaskDaemon daemon = mock(LightTaskDaemon.class);

			LightTask lightTask = new LightTask();
			lightTask.setDaemon(daemon);
			lightTask.run();
		} catch (Throwable t) {
			t.printStackTrace();
			fail("Unexpected error:" + t.getMessage());
		}
	}

	@Test
	public void testPlanPercentagePresent() {

		LightTaskDaemon daemon = mock(LightTaskDaemon.class);

		LightEnvironment environment = mock(LightEnvironment.class);
		when(daemon.getLightEnvironment()).thenReturn(environment);

		LightEnvironmentChannel environmentChannel = mock(LightEnvironmentChannel.class);
		when(environment.channel("0x40")).thenReturn(environmentChannel);

		DimmingPlan plan = mock(DimmingPlan.class);
		Set<String> channelNames = new HashSet<>();
		channelNames.add("0x40");
		DimmingPlanChannel channel = mock(DimmingPlanChannel.class);
		when(plan.getChannelNames()).thenReturn(channelNames);
		when(plan.channel("0x40")).thenReturn(channel);
		LocalTime time = LocalTime.of(12, 0);
		when(channel.getPercentage(time)).thenReturn(OptionalDouble.of(100.0d));
		when(daemon.getLightPlan()).thenReturn(plan);

		LightTask lightTask = new LightTask();
		lightTask.setDaemon(daemon);

		lightTask.executePlanFor(time);
		verify(environment).channel(eq("0x40"));
		verifyNoMoreInteractions(environment);
		verify(environmentChannel).percentage(eq(100.0d, 0.01d));
		verifyNoMoreInteractions(environmentChannel);
	}

	@Test
	public void testPlanPercentageNotPresent() {

		LightTaskDaemon daemon = mock(LightTaskDaemon.class);

		LightEnvironment environment = mock(LightEnvironment.class);
		when(daemon.getLightEnvironment()).thenReturn(environment);

		DimmingPlan plan = mock(DimmingPlan.class);
		Set<String> channelNames = new HashSet<>();
		channelNames.add("0x40");
		DimmingPlanChannel channel = mock(DimmingPlanChannel.class);
		when(plan.getChannelNames()).thenReturn(channelNames);
		when(plan.channel("0x40")).thenReturn(channel);
		LocalTime time = LocalTime.of(12, 0);
		when(channel.getPercentage(time)).thenReturn(OptionalDouble.empty());
		when(daemon.getLightPlan()).thenReturn(plan);

		LightTask lightTask = new LightTask();
		lightTask.setDaemon(daemon);

		lightTask.executePlanFor(time);
		verifyZeroInteractions(environment);
	}
}
