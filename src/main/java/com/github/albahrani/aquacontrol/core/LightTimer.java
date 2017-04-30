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

import java.util.Objects;
import java.util.Optional;
import java.util.Timer;

public class LightTimer {
	private Optional<LightTask> task = Optional.empty();
	private Timer timer;

	public LightTimer() {
		timer = new Timer("LightTimer", true);
	}

	public void setTimer(Timer timer) {
		Objects.requireNonNull(timer);
		this.timer = timer;
	}

	public void start(LightTaskDaemon daemon) {
		Objects.requireNonNull(daemon);
		if (!task.isPresent()) {
			LightTask newTask = new LightTask();
			newTask.setDaemon(daemon);
			timer.scheduleAtFixedRate(newTask, 100, 1000);
			this.task = Optional.of(newTask);
		}
	}

	public void shutdown() {
		this.stop();
		this.timer.cancel();
	}

	public void stop() {
		this.task.ifPresent(LightTask::cancel);
		this.task = Optional.empty();
	}
}
