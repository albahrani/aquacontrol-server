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

import com.github.albahrani.aquacontrol.logger.Logger;
import org.restexpress.Request;
import org.restexpress.Response;

import com.github.albahrani.aquacontrol.core.LightEnvironment;
import com.github.albahrani.aquacontrol.core.LightEnvironmentChannel;
import com.github.albahrani.aquacontrol.core.LightEnvironmentChannelBuilder;
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
					.collect(Collectors.toMap(LightEnvironmentChannel::id, channel -> {
						JSONConfigurationChannel channelDef = new JSONConfigurationChannel();
						channelDef.setName(channel.name());
						channelDef.setColor(channel.color());
						channelDef.setPins(channel.pins().map(Pin::getName).collect(Collectors.toList()));
						return channelDef;
					}));

			response.setBody(channelIdsToName);
			response.setResponseStatus(HttpResponseStatus.OK);
		} catch (Exception e) {
			Logger.error(e, "Unexpected error while getChannels.");
			response.setException(e);
		}
	}

	public void addChannel(Request request, Response response) {
		if (!CORSHelper.handleCORS(request, response)) {
			return;
		}

		try {
			String channelId = request.getHeader("channelId", "Channel Id is missing.");
			JSONConfigurationChannel channelDef = request.getBodyAs(JSONConfigurationChannel.class);
			System.out.println(channelDef.getPins());
			LightEnvironment lightEnvironment = this.daemon.getLightEnvironment();
			LightEnvironmentChannelBuilder channelBuilder = LightEnvironmentChannel
					.create(channelId, lightEnvironment.getPwmControllerConnector())
					.withName(channelDef.getName())
					.withColor(channelDef.getColor());
			channelDef.getPins().forEach(channelBuilder::usePin);
			lightEnvironment.addChannel(channelId, channelBuilder.build());

			response.setResponseStatus(HttpResponseStatus.OK);
		} catch (Exception e) {
			Logger.error(e, "Unexpected error while adding channel.");
			response.setException(e);
		}
	}
	
	public void updateChannel(Request request, Response response) {
		if (!CORSHelper.handleCORS(request, response)) {
			return;
		}

		try {
			String channelId = request.getHeader("channelId", "Channel Id is missing.");
			JSONConfigurationChannel channelDef = request.getBodyAs(JSONConfigurationChannel.class);

			LightEnvironment lightEnvironment = this.daemon.getLightEnvironment();
			LightEnvironmentChannelBuilder channelBuilder = LightEnvironmentChannel
					.create(channelId, lightEnvironment.getPwmControllerConnector())
					.withName(channelDef.getName())
					.withColor(channelDef.getColor());
			channelDef.getPins().forEach(channelBuilder::usePin);
			lightEnvironment.updateChannel(channelId, channelBuilder.build());

			response.setResponseStatus(HttpResponseStatus.OK);
		} catch (Exception e) {
			Logger.error(e, "Unexpected error while updating channel.");
			response.setException(e);
		}
	}
	
	public void deleteChannel(Request request, Response response) {
		if (!CORSHelper.handleCORS(request, response)) {
			return;
		}

		try {
			String channelId = request.getHeader("channelId", "Channel Id is missing.");

			LightEnvironment lightEnvironment = this.daemon.getLightEnvironment();
			lightEnvironment.removeChannel(channelId);

			response.setResponseStatus(HttpResponseStatus.OK);
		} catch (Exception e) {
			Logger.error(e, "Unexpected error while deleting channel.");
			response.setException(e);
		}
	}

	public void preflight(Request request, Response response) {
		if (!CORSHelper.handleCORS(request, response)) {
			return;
		}
	}
}
