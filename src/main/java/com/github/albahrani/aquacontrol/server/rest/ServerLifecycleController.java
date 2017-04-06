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

import org.pmw.tinylog.Logger;
import org.restexpress.Request;
import org.restexpress.Response;

import com.github.albahrani.aquacontrol.server.LightServerController;

import io.netty.handler.codec.http.HttpResponseStatus;

public class ServerLifecycleController {

	private LightServerController daemon;

	public ServerLifecycleController(LightServerController daemon) {
		this.daemon = daemon;
	}

	public void shutdown(Request request, Response response) {
		try {
			this.daemon.shutdown();
			response.setResponseStatus(HttpResponseStatus.OK);

		} catch (Exception t) {
			Logger.error(t, "Unexpected error.");
			response.setException(t);
		}
	}

	public void pause(Request request, Response response) {
		try {
			this.daemon.pause();
			response.setResponseStatus(HttpResponseStatus.OK);

		} catch (Exception t) {
			Logger.error(t, "Unexpected error.");
			response.setException(t);
		}
	}

	public void resume(@SuppressWarnings("unused") Request request, Response response) {
		try {
			this.daemon.resume();
			response.setResponseStatus(HttpResponseStatus.OK);

		} catch (Exception t) {
			Logger.error(t, "Unexpected error.");
			response.setException(t);
		}
	}
}
