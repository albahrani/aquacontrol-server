/**
 * Copyright © 2015 albahrani (https://github.com/albahrani)
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

import java.io.File;
import java.util.Objects;

import com.github.albahrani.aquacontrol.core.LightEnvironment;
import com.github.albahrani.aquacontrol.core.LightTaskDaemon;
import com.github.albahrani.aquacontrol.core.LightTimer;
import com.github.albahrani.aquacontrol.server.json.JSONPlan;
import com.github.albahrani.aquacontrol.server.rest.RESTServer;
import com.github.albahrani.dimmingplan.DimmingPlan;

/**
 * Hello world!
 *
 */
public class LightServerController implements LightTaskDaemon {
	private RESTServer server;
	private LightTimer timer;
	private LightEnvironment lightEnvironment;

	private File lightPlanFile = null;
	private DimmingPlan lightPlan = null;
	private JSONPlan jsonLightPlan = null;

	public LightServerController() {
		this.server = new RESTServer();
		this.server.setDaemon(this);
		this.timer = new LightTimer();
	}

	public void setLightEnvironment(LightEnvironment lightEnvironment) {
		Objects.requireNonNull(lightEnvironment);
		this.lightEnvironment = lightEnvironment;
	}

	public void setLightPlanFile(File lightPlanFile) {
		Objects.requireNonNull(lightPlanFile);
		this.lightPlanFile = lightPlanFile;
	}

	public File getLightPlanFile() {
		return lightPlanFile;
	}

	public JSONPlan getJsonLightPlan() {
		return this.jsonLightPlan;
	}

	public void setLightPlan(DimmingPlan lightPlan, JSONPlan jsonLightPlan) {
		Objects.requireNonNull(lightPlan);
		Objects.requireNonNull(jsonLightPlan);
		this.lightPlan = lightPlan;
		this.jsonLightPlan = jsonLightPlan;
	}

	@Override
	public LightEnvironment getLightEnvironment() {
		return this.lightEnvironment;
	}

	@Override
	public DimmingPlan getLightPlan() {
		return lightPlan;
	}

	public void setTimer(LightTimer timer) {
		this.timer = timer;
	}

	public void setServer(RESTServer server) {
		this.server = server;
	}

	public void start() {
		System.out.println("Starting LightTimer");
		this.timer.start(this);

		System.out.println("Starting RESTServer");
		this.server.start();
	}

	public void resume() {
		System.out.println("Resuming LightTimer");
		this.timer.start(this);
	}

	public void pause() {
		System.out.println("Pausing LightTimer");
		this.timer.stop();
	}

	public void shutdown() {
		this.server.shutdown();
		this.timer.shutdown();
		this.lightEnvironment.shutdown();
	}

	public void setForcedValue(String channelId, double channelValue) {
		this.lightPlan.channel(channelId).pin(channelValue);
	}

	public void clearForcedValue(String channelId) {
		this.lightPlan.channel(channelId).unpin();
	}
}
