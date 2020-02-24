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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.albahrani.aquacontrol.logger.Logger;
import com.github.albahrani.aquacontrol.server.json.JSONPlan;
import com.github.albahrani.dimmingplan.DimmingPlan;
import com.github.albahrani.dimmingplan.DimmingPlanChannel;

public class LightPlanStorage {

	private Optional<File> lightPlanFile = Optional.empty();
	private JSONPlan jsonLightPlan = new JSONPlan();

	public Optional<File> getLightPlanFile() {
		return this.lightPlanFile;
	}

	public JSONPlan read(InputStream lightPlanInputStream) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(lightPlanInputStream, JSONPlan.class);
	}

	public DimmingPlan loadLightPlanFromFile(Optional<File> lightPlanFile) {
		this.lightPlanFile = lightPlanFile;

		if (lightPlanFile.isPresent()) {
			try (FileInputStream fis = new FileInputStream(lightPlanFile.get())) {
				this.jsonLightPlan = this.read(fis);
			} catch (IOException e) {
				Logger.error(e, "Could not load plan from {}. Either not available or invalid format.", lightPlanFile);
				this.jsonLightPlan = new JSONPlan();
			}
		} else {
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

	public boolean storeLightPlanToFile(Optional<File> lightPlanPath) {
		this.lightPlanFile = lightPlanPath;
		boolean success = false;
		
		if (this.lightPlanFile.isPresent()) {
			try (FileWriter writer = new FileWriter(this.lightPlanFile.get())) {
				success = this.write(this.jsonLightPlan, writer);
			} catch (IOException e) {
				Logger.error(e, "Error in FileWriter storing plan to file {}.", this.lightPlanFile);
			}
		} else {
			success = true;
		}
		return success;
	}

	public boolean write(JSONPlan restPlan, Writer writer) {
		boolean success = false;

		ObjectMapper jacksonMapper = new ObjectMapper();
		ObjectWriter jacksonWriter = jacksonMapper.writer(new DefaultPrettyPrinter());
		
		try{
			jacksonWriter.writeValue(writer, restPlan);
			success = true;
		} catch(IOException e) {
			Logger.error(e, "Error in jackson storing plan to file {}.", this.lightPlanFile);
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
