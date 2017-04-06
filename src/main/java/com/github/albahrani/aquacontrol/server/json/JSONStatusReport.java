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
package com.github.albahrani.aquacontrol.server.json;

import java.util.Map;
import java.util.Set;

public class JSONStatusReport {

	private Set<String> channelIds = null;

	private Map<String, JSONChannelState> lightEnvironment = null;

	private String uptime = null;

	private String freeMemory = null;

	private String totalMemory = null;

	private String availableProcessors = null;

	public void setLightEnvironment(Map<String, JSONChannelState> envReport) {
		this.lightEnvironment = envReport;
	}

	public Map<String, JSONChannelState> getLightEnvironment() {
		return lightEnvironment;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
	}

	public String getUptime() {
		return uptime;
	}

	public void setFreeMemory(String freeMemory) {
		this.freeMemory = freeMemory;
	}

	public String getFreeMemory() {
		return freeMemory;
	}

	public void setTotalMemory(String totalMemory) {
		this.totalMemory = totalMemory;
	}

	public String getTotalMemory() {
		return totalMemory;
	}

	public void setAvailableProcessors(String availableProcessors) {
		this.availableProcessors = availableProcessors;
	}

	public String getAvailableProcessors() {
		return availableProcessors;
	}

	public void setChannelIds(Set<String> channels) {
		this.channelIds = channels;
	}

	public Set<String> getChannelIds() {
		return channelIds;
	}
}
