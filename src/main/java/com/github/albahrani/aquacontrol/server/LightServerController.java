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

import java.io.File;
import java.util.Objects;
import java.util.Optional;

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
	private LightPlanStorage lightPlanStorage;

	private DimmingPlan lightPlan = null;

	public LightServerController() {
		this.server = new RESTServer();
		this.server.setDaemon(this);
		this.timer = new LightTimer();
	}

	public void setLightEnvironment(LightEnvironment lightEnvironment) {
		Objects.requireNonNull(lightEnvironment);
		this.lightEnvironment = lightEnvironment;
	}

	public void setLightPlanStorage(LightPlanStorage lightPlanStorage) {
		Objects.requireNonNull(lightPlanStorage);
		this.lightPlanStorage = lightPlanStorage;
	}

	public void loadLightPlanFromFile(Optional<File> lightPlanFile) {
		this.lightPlan = this.lightPlanStorage.loadLightPlanFromFile(lightPlanFile);
	}

	public void updateLightPlan(JSONPlan jsonPlan) {
		this.lightPlanStorage.setJsonLightPlan(jsonPlan);
		this.lightPlanStorage.storeLightPlanToFile(this.lightPlanStorage.getLightPlanFile());
	}

	void setLightPlan(DimmingPlan lightPlan) {
		this.lightPlan = lightPlan;
	}

	@Override
	public LightEnvironment getLightEnvironment() {
		return this.lightEnvironment;
	}

	@Override
	public DimmingPlan getLightPlan() {
		return lightPlan;
	}

	void setTimer(LightTimer timer) {
		this.timer = timer;
	}

	void setServer(RESTServer server) {
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

	public JSONPlan getJsonLightPlan() {
		return this.lightPlanStorage.getJsonLightPlan();
	}
}
