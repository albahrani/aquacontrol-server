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
package com.github.albahrani.aquacontrol.server.rest;

import org.pmw.tinylog.Logger;
import org.restexpress.Request;
import org.restexpress.Response;

import com.github.albahrani.aquacontrol.server.LightServerController;
import com.github.albahrani.aquacontrol.server.json.JSONForceValue;

import io.netty.handler.codec.http.HttpResponseStatus;

public class ForcedValuesController {

	private LightServerController daemon;

	public ForcedValuesController(LightServerController daemon) {
		this.daemon = daemon;
	}

	public void forceValue(Request request, Response response) {
		if (!CORSHelper.handleCORS(request, response)) {
			return;
		}

		try {
			String channelId = request.getHeader("channelId", "Channel Id is missing.");

			JSONForceValue body = request.getBodyAs(JSONForceValue.class);

			this.daemon.setForcedValue(channelId, body.getValue());

			response.setResponseStatus(HttpResponseStatus.OK);
		} catch (Exception t) {
			Logger.error(t, "Unexpected error.");
			response.setException(t);
		}
	}

	public void clearForcedValue(Request request, Response response) {
		if (!CORSHelper.handleCORS(request, response)) {
			return;
		}

		try {

			String channelId = request.getHeader("channelId", "Channel Id is missing.");

			this.daemon.clearForcedValue(channelId);

			response.setResponseStatus(HttpResponseStatus.OK);
		} catch (Exception t) {
			Logger.error(t, "Unexpected error.");
			response.setException(t);
		}
	}
}
