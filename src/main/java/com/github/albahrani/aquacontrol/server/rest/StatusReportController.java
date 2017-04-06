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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.pmw.tinylog.Logger;
import org.restexpress.Request;
import org.restexpress.Response;

import com.github.albahrani.aquacontrol.core.LightEnvironment;
import com.github.albahrani.aquacontrol.core.LightEnvironmentChannel;
import com.github.albahrani.aquacontrol.server.LightServerController;
import com.github.albahrani.aquacontrol.server.json.JSONChannelState;
import com.github.albahrani.aquacontrol.server.json.JSONStatusReport;

import io.netty.handler.codec.http.HttpResponseStatus;

public class StatusReportController {

	private LightServerController daemon;

	public StatusReportController(LightServerController daemon) {
		this.daemon = daemon;
	}

	public void getStatus(Request request, Response response) {
		if (!CORSHelper.handleCORS(request, response)) {
			return;
		}

		try {
			LightEnvironment lightEnvironment = this.daemon.getLightEnvironment();
			JSONStatusReport report = new JSONStatusReport();

			Set<String> channelIds = lightEnvironment.channels().map((channel) -> {
				return channel.id();
			}).collect(Collectors.toSet());

			Map<String, JSONChannelState> envReport = lightEnvironment.channels().collect(Collectors.toMap(LightEnvironmentChannel::id, (c) -> {
				JSONChannelState state = new JSONChannelState();
				state.setName(c.name());
				state.setValue(c.lastValue());
				return state;
			}));

			report.setChannelIds(channelIds);
			report.setLightEnvironment(envReport);

			String uptime = StatusReportController.getUptime();
			report.setUptime(uptime);

			Runtime runtime = Runtime.getRuntime();
			report.setFreeMemory(formatMemory(runtime.freeMemory()));
			report.setTotalMemory(formatMemory(runtime.totalMemory()));
			report.setAvailableProcessors(formatAvailableProcessors(runtime.availableProcessors()));

			response.setBody(report);
			response.setResponseStatus(HttpResponseStatus.OK);
		} catch (Exception t) {
			Logger.error(t, "Unexpected error.");
			response.setException(t);
		}
	}

	static String formatAvailableProcessors(int availableProcessors) {
		return String.format("%d Core(s)", availableProcessors);
	}

	static String formatMemory(long memory) {
		return String.format("%.2f MB", (memory / 1048576f));
	}

	static String getUptime() {
		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		long millis = runtimeBean.getUptime();
		return StatusReportController.formatUptime(millis);
	}

	public static String formatUptime(long millis) {
		long totalMillis = millis;
		long days = TimeUnit.MILLISECONDS.toDays(totalMillis);
		totalMillis -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(totalMillis);
		totalMillis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(totalMillis);
		totalMillis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(totalMillis);

		StringBuilder uptimeStr = new StringBuilder();
		if (StatusReportController.getUnitDescr(uptimeStr, days, "days", "day", null)) {
			uptimeStr.append(" ");
		}
		if (StatusReportController.getUnitDescr(uptimeStr, hours, "hours", "hour", null)) {
			uptimeStr.append(" ");
		}
		if (StatusReportController.getUnitDescr(uptimeStr, minutes, "min", "min", "min")) {
			uptimeStr.append(" ");
		}
		StatusReportController.getUnitDescr(uptimeStr, seconds, "sec", "sec", "sec");
		return uptimeStr.toString();
	}

	private static boolean getUnitDescr(StringBuilder uptimeStr, long value, String multiple, String single, String zero) {
		boolean appended = false;
		if (value == 1) {
			uptimeStr.append(value);
			uptimeStr.append(" ");
			uptimeStr.append(single);
			appended = true;
		} else if ((value > 1) || ((zero != null) || (uptimeStr.length() != 0))) {
			uptimeStr.append(value);
			uptimeStr.append(" ");
			uptimeStr.append(multiple);
			appended = true;
		}

		return appended;
	}
}
