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

import com.github.albahrani.aquacontrol.logger.Logger;
import org.restexpress.Request;
import org.restexpress.Response;

import com.github.albahrani.aquacontrol.server.LightServerController;
import com.github.albahrani.aquacontrol.server.json.JSONPlan;

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

			JSONPlan jsonPlan = request.getBodyAs(JSONPlan.class);
			this.daemon.updateLightPlan(jsonPlan);

			response.setResponseStatus(HttpResponseStatus.OK);

		} catch (Exception t) {
			Logger.error(t, "Unexpected error.");
			response.setException(t);
		}
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
