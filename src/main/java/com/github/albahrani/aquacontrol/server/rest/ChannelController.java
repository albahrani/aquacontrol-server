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

import java.util.Map;
import java.util.stream.Collectors;

import org.pmw.tinylog.Logger;
import org.restexpress.Request;
import org.restexpress.Response;

import com.github.albahrani.aquacontrol.core.LightEnvironment;
import com.github.albahrani.aquacontrol.core.LightEnvironmentChannel;
import com.github.albahrani.aquacontrol.server.LightServerController;
import com.github.albahrani.aquacontrol.server.json.JSONConfigurationChannel;
import com.pi4j.io.gpio.Pin;

import io.netty.handler.codec.http.HttpResponseStatus;

public class ChannelController {

	private LightServerController daemon;

	public ChannelController(LightServerController daemon) {
		this.daemon = daemon;
	}

	public void getChannels(Request request, Response response) {
		if (!CORSHelper.handleCORS(request, response)) {
			return;
		}

		try {
			LightEnvironment lightEnvironment = this.daemon.getLightEnvironment();

			Map<String, JSONConfigurationChannel> channelIdsToName = lightEnvironment.channels()
					.collect(Collectors.toMap(LightEnvironmentChannel::id, (channel) -> {
						JSONConfigurationChannel channelDef = new JSONConfigurationChannel();
						channelDef.setName(channel.name());
						channelDef.setPins(channel.pins().map(Pin::getName).collect(Collectors.toList()));
						return channelDef;
					}));

			response.setBody(channelIdsToName);
			response.setResponseStatus(HttpResponseStatus.OK);
		} catch (Exception e) {
			Logger.error(e, "Unexpected error.");
			response.setException(e);
		}
	}

	public void preflight(Request request, Response response) {
		if (!CORSHelper.handleCORS(request, response)) {
			return;
		}
	}
}
