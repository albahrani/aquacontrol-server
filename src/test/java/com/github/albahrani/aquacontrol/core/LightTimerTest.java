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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.Timer;

import org.junit.Test;

import com.github.albahrani.aquacontrol.core.LightTask;
import com.github.albahrani.aquacontrol.core.LightTaskDaemon;
import com.github.albahrani.aquacontrol.core.LightTimer;

public class LightTimerTest {

	@Test
	public void testStart() {
		Timer timer = mock(Timer.class);
		LightTaskDaemon daemon = mock(LightTaskDaemon.class);

		LightTimer lightTimer = new LightTimer();
		lightTimer.setTimer(timer);

		lightTimer.start(daemon);

		verify(timer).scheduleAtFixedRate(any(LightTask.class), eq(100l), eq(500l));
		verifyNoMoreInteractions(timer);
		verifyZeroInteractions(daemon);
	}

	@Test
	public void testStartTwice() {
		Timer timer = mock(Timer.class);
		LightTaskDaemon daemon = mock(LightTaskDaemon.class);

		LightTimer lightTimer = new LightTimer();
		lightTimer.setTimer(timer);

		lightTimer.start(daemon);
		lightTimer.start(daemon);

		verify(timer).scheduleAtFixedRate(any(LightTask.class), eq(100l), eq(500l));
		verifyNoMoreInteractions(timer);
		verifyZeroInteractions(daemon);
	}

	@Test
	public void testStopWithoutStart() {
		Timer timer = mock(Timer.class);
		LightTaskDaemon daemon = mock(LightTaskDaemon.class);

		LightTimer lightTimer = new LightTimer();
		lightTimer.setTimer(timer);

		lightTimer.stop();

		verifyZeroInteractions(timer);
		verifyZeroInteractions(daemon);
	}

	@Test
	public void testStartStop() {
		Timer timer = mock(Timer.class);
		LightTaskDaemon daemon = mock(LightTaskDaemon.class);

		LightTimer lightTimer = new LightTimer();
		lightTimer.setTimer(timer);

		lightTimer.start(daemon);
		lightTimer.stop();

		verify(timer).scheduleAtFixedRate(any(LightTask.class), eq(100l), eq(500l));
		verifyNoMoreInteractions(timer);
		verifyZeroInteractions(daemon);
	}

	@Test
	public void testShutdownWithoutStart() {
		Timer timer = mock(Timer.class);
		LightTaskDaemon daemon = mock(LightTaskDaemon.class);

		LightTimer lightTimer = new LightTimer();
		lightTimer.setTimer(timer);

		lightTimer.shutdown();

		verify(timer).cancel();
		verifyNoMoreInteractions(timer);
		verifyZeroInteractions(daemon);
	}

}
