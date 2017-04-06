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

import org.restexpress.RestExpress;
import org.restexpress.pipeline.SimpleConsoleLogMessageObserver;

import com.github.albahrani.aquacontrol.server.LightServer;
import com.github.albahrani.aquacontrol.server.LightServerController;

import de.albahrani.pi4j.rest.sysinfo.RaspberrySystemInfoController;
import io.netty.handler.codec.http.HttpMethod;

public class RESTServer {

	private RestExpress server;
	private LightServerController daemon;

	public RESTServer() {
	}

	public void setDaemon(LightServerController daemon) {
		this.daemon = daemon;
	}

	public void start() {
		this.server = new RestExpress();
		this.server.setName("LightDaemonRESTServer");
		this.server.setUseSystemOut(true);
		this.server.uri("/plan", new PlanController(this.daemon)).action("upload", HttpMethod.POST).action("get", HttpMethod.GET).action("preflight",
				HttpMethod.OPTIONS);
		this.server.uri("/channels", new ChannelController(this.daemon)).action("getChannels", HttpMethod.GET).action("preflight", HttpMethod.OPTIONS);
		ServerLifecycleController lifecycleController = new ServerLifecycleController(this.daemon);
		this.server.uri("/shutdown", lifecycleController).action("shutdown", HttpMethod.PUT);
		this.server.uri("/pause", lifecycleController).action("pause", HttpMethod.PUT);
		this.server.uri("/resume", lifecycleController).action("resume", HttpMethod.PUT);
		ForcedValuesController forcedValueController = new ForcedValuesController(this.daemon);
		this.server.uri("/channels/{channelId}/force", forcedValueController).action("forceValue", HttpMethod.POST).action("forceValue", HttpMethod.OPTIONS);
		this.server.uri("/channels/{channelId}/clear", forcedValueController).action("clearForcedValue", HttpMethod.DELETE).action("clearForcedValue",
				HttpMethod.OPTIONS);
		StatusReportController statusReportController = new StatusReportController(daemon);
		this.server.uri("/status", statusReportController).action("getStatus", HttpMethod.GET).action("getStatus", HttpMethod.OPTIONS);

		if (LightServer.isRunningOnRaspberry()) {
			RaspberrySystemInfoController raspberrySystemInfoController = new RaspberrySystemInfoController();
			raspberrySystemInfoController.attach(this.server, "/raspberry/systeminfo");
		}

		this.server.addMessageObserver(new SimpleConsoleLogMessageObserver());

		this.server.bind();
	}

	public void shutdown() {
		this.server.shutdown();
	}
}
