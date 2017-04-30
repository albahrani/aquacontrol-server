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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

import org.pmw.tinylog.Logger;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.albahrani.aquacontrol.server.json.JSONPlan;
import com.github.albahrani.dimmingplan.DimmingPlan;
import com.github.albahrani.dimmingplan.DimmingPlanChannel;

public class LightPlanStorage {

	private File lightPlanFile;
	private JSONPlan jsonLightPlan;

	public File getLightPlanFile() {
		return this.lightPlanFile;
	}

	public DimmingPlan loadLightPlanFromFile(File lightPlanFile) {
		this.lightPlanFile = lightPlanFile;

		ObjectMapper mapper = new ObjectMapper();
		try {
			this.jsonLightPlan = mapper.readValue(lightPlanFile, JSONPlan.class);
		} catch (IOException e) {
			Logger.error(e, "Could not load plan from {}. Either not available or invalid format.", lightPlanFile);
			this.jsonLightPlan = new JSONPlan();
		}

		return fromJSON(this.jsonLightPlan);
	}

	public static DimmingPlan fromJSON(JSONPlan restPlan) {
		Objects.requireNonNull(restPlan);
		DimmingPlan plan = DimmingPlan.create();
		restPlan.getChannels().forEach(restChannel -> {
			DimmingPlanChannel channel = plan.channel(restChannel.getId());
			restChannel.getTimetable().forEach(restTvp -> channel.define(restTvp.getTime(), restTvp.getPerc()));
		});

		return plan;
	}

	public boolean storeLightPlanToFile(File lightPlanPath) {
		this.lightPlanFile = lightPlanPath;
		boolean success = false;
		try (FileWriter writer = new FileWriter(this.lightPlanFile)) {
			success = this.write(this.jsonLightPlan, writer);
		} catch (IOException e) {
			Logger.error(e, "Error storing plan to file {}.", this.lightPlanFile);
		}
		return success;
	}

	public boolean write(JSONPlan restPlan, Writer writer) {
		boolean success = false;
		ObjectMapper jacksonMapper = new ObjectMapper();
		ObjectWriter jacksonWriter = jacksonMapper.writer(new DefaultPrettyPrinter());
		try {
			jacksonWriter.writeValue(writer, restPlan);
			success = true;
		} catch (IOException e) {
			Logger.error(e, "Error writing plan to writer.");
		}
		return success;
	}

	public void setJsonLightPlan(JSONPlan jsonLightPlan) {
		this.jsonLightPlan = jsonLightPlan;
	}

	public JSONPlan getJsonLightPlan() {
		return this.jsonLightPlan;
	}

}
