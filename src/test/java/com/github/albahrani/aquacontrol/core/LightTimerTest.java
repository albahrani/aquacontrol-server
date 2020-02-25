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

import com.github.albahrani.aquacontrol.logger.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LightTimerTest {

	@BeforeClass
	public static void beforeClass() {
		Logger.setActive(false);
	}

	private Timer timer = mock(Timer.class);
	private LightTaskDaemon daemon = mock(LightTaskDaemon.class);
	private LightTimer lightTimer = new LightTimer();

	@Before
	public void before() {
		lightTimer.setTimer(timer);
	}

	@Test
	public void testStart() {
		lightTimer.start(daemon);

		verify(timer).scheduleAtFixedRate(any(LightTask.class), eq(100l), eq(1000l));
		verifyNoMoreInteractions(timer, daemon);
	}

	@Test
	public void testStartTwice() {
		lightTimer.start(daemon);
		lightTimer.start(daemon);

		verify(timer).scheduleAtFixedRate(any(LightTask.class), eq(100l), eq(1000l));
		verifyNoMoreInteractions(timer, daemon);
	}

	@Test
	public void testStopWithoutStart() {
		lightTimer.stop();

		verifyNoMoreInteractions(timer, daemon);
	}

	@Test
	public void testStartStop() {
		lightTimer.start(daemon);
		lightTimer.stop();

		verify(timer).scheduleAtFixedRate(any(LightTask.class), eq(100l), eq(1000l));
		verifyNoMoreInteractions(timer, daemon);
	}

	@Test
	public void testShutdownWithoutStart() {
		lightTimer.shutdown();

		verify(timer).cancel();
		verifyNoMoreInteractions(timer, daemon);
	}

}
