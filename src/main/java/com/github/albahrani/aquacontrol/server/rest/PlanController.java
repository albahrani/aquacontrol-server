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
package com.github.albahrani.aquacontrol.server.rest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.pmw.tinylog.Logger;
import org.restexpress.Request;
import org.restexpress.Response;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.albahrani.aquacontrol.server.LightServer;
import com.github.albahrani.aquacontrol.server.LightServerController;
import com.github.albahrani.aquacontrol.server.json.JSONPlan;
import com.github.albahrani.dimmingplan.DimmingPlan;

import io.netty.handler.codec.http.HttpResponseStatus;

public class PlanController {

	private LightServerController daemon;

	public PlanController(LightServerController daemon) {
		this.daemon = daemon;
	}

	public void upload(Request request, Response response) {
		if (!CORSHelper.handleCORS(request, response)) {
			return;
		}

		try {

			JSONPlan restPlan = request.getBodyAs(JSONPlan.class);

			DimmingPlan lightPlan = LightServer.fromJSON(restPlan);
			this.daemon.setLightPlan(lightPlan, restPlan);

			this.store(restPlan, this.daemon.getLightPlanFile());
			response.setResponseStatus(HttpResponseStatus.OK);

		} catch (Exception t) {
			Logger.error(t, "Unexpected error.");
			response.setException(t);
		}
	}

	private boolean store(JSONPlan restPlan, File lightPlanPath) {
		boolean success = false;
		try (FileWriter writer = new FileWriter(lightPlanPath)) {
			success = this.write(restPlan, writer);
		} catch (IOException e) {
			Logger.error(e, "Error storing plan to file {}.", lightPlanPath);
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

	public void get(Request request, Response response) {
		if (!CORSHelper.handleCORS(request, response)) {
			return;
		}

		response.setBody(this.daemon.getJsonLightPlan());
		response.setResponseStatus(HttpResponseStatus.OK);
	}

	public void preflight(Request request, Response response) {
		if (!CORSHelper.handleCORS(request, response)) {
			return;
		}
	}
}
