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

import java.time.LocalTime;
import java.util.Objects;
import java.util.TimerTask;

import com.github.albahrani.aquacontrol.logger.Logger;
import com.github.albahrani.dimmingplan.DimmingPlan;

public class LightTask extends TimerTask {

	private LightTaskDaemon daemon = null;

	public void setDaemon(LightTaskDaemon daemon) {
		Objects.requireNonNull(daemon);
		this.daemon = daemon;
	}

	@Override
	public void run() {
		try {
			LocalTime now = LocalTime.now();
			this.executePlanFor(now);
		} catch (Exception e) {
			Logger.error(e, "Unexpected termination of LightTask.");
		}
	}

	public void executePlanFor(LocalTime planTime) {
		Objects.requireNonNull(planTime);
		DimmingPlan plan = this.daemon.getLightPlan();
		LightEnvironment environment = this.daemon.getLightEnvironment();
		environment.channels().forEach(envChannel -> envChannel.percentage(plan.channel(envChannel.getId()).getPercentage(planTime).orElse(0.0d)));
	}
}
