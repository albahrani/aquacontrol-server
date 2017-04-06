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
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.pmw.tinylog.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.albahrani.aquacontrol.core.LightEnvironment;
import com.github.albahrani.aquacontrol.core.LightEnvironmentBuilder;
import com.github.albahrani.aquacontrol.core.LightEnvironmentChannel;
import com.github.albahrani.aquacontrol.core.LightEnvironmentChannelBuilder;
import com.github.albahrani.aquacontrol.core.environment.LightEnvironmentChannelConfiguration;
import com.github.albahrani.aquacontrol.core.environment.LightEnvironmentConfiguration;
import com.github.albahrani.aquacontrol.core.environment.PWMControllerConnector;
import com.github.albahrani.aquacontrol.core.environment.dummy.PWMControllerConnectorDummy;
import com.github.albahrani.aquacontrol.core.environment.production.PCA9685Connector;
import com.github.albahrani.aquacontrol.server.config.InvalidConfigurationException;
import com.github.albahrani.aquacontrol.server.config.LightServerConfigurationFactory;
import com.github.albahrani.aquacontrol.server.json.JSONPlan;
import com.github.albahrani.dimmingplan.DimmingPlan;
import com.github.albahrani.dimmingplan.DimmingPlanChannel;
import com.pi4j.system.SystemInfo;

/**
 * Hello world!
 *
 */
public class LightServer {
	public static void main(String[] args) {
		LightServerArgs cliArguments = parseArgs(args);
		if (cliArguments == null) {
			return;
		}
		if (cliArguments.isShowUsage()) {
			cliArguments.showUsage();
			Logger.error("Usage information is shown. Not starting application.");
			return;
		}

		LightEnvironmentConfiguration configuration = null;
		try {
			configuration = LightServerConfigurationFactory.loadConfiguration(cliArguments.getConfigFile());
			if (configuration == null) {
				Logger.error("Configuration could not be loaded from '{}'.", cliArguments.getConfigFile());
				return;
			}
		} catch (InvalidConfigurationException e) {
			Logger.error(e, "Configuration could not be loaded from '{}'.", cliArguments.getConfigFile());
			return;
		}

		LightServerController daemon = new LightServerController();
		Runtime.getRuntime().addShutdownHook(new LightDaemonShutdownHook(daemon));

		daemon.setLightPlanFile(cliArguments.getLightPlanFile());

		boolean runningOnRaspberry = isRunningOnRaspberry();

		// Init light environment
		LightEnvironment env = initLightEnvironemnt(configuration, runningOnRaspberry);
		daemon.setLightEnvironment(env);

		// Init plan
		JSONPlan jsonPlan = readLightPlanFile(cliArguments.getLightPlanFile()).orElse(new JSONPlan());
		DimmingPlan plan = fromJSON(jsonPlan);
		daemon.setLightPlan(plan, jsonPlan);
		daemon.start();

	}

	public static LightServerArgs parseArgs(String[] args) {
		LightServerArgs cliArguments = new LightServerArgs();
		boolean parse = cliArguments.parse(args);
		if (!parse) {
			return null;
		}

		return cliArguments;
	}

	private static Optional<JSONPlan> readLightPlanFile(File lightPlanFile) {
		Objects.requireNonNull(lightPlanFile);
		Optional<JSONPlan> restPlan = Optional.empty();
		ObjectMapper mapper = new ObjectMapper();
		try {
			restPlan = Optional.of(mapper.readValue(lightPlanFile, JSONPlan.class));
		} catch (IOException e) {
			Logger.error(e, "Could not load plan from {}. Either not available or invalid format.", lightPlanFile);
		}
		return restPlan;
	}

	public static DimmingPlan fromJSON(JSONPlan restPlan) {
		Objects.requireNonNull(restPlan);
		DimmingPlan plan = DimmingPlan.create();
		restPlan.getChannels().forEach((restChannel) -> {
			DimmingPlanChannel channel = plan.channel(restChannel.getId());
			restChannel.getTimetable().forEach((restTvp) -> {
				channel.define(restTvp.getTime(), restTvp.getPerc());
			});
		});

		return plan;
	}

	private static LightEnvironment initLightEnvironemnt(LightEnvironmentConfiguration configuration, boolean runningOnRaspberry) {
		Objects.requireNonNull(configuration);
		PWMControllerConnector pwmControllerConnector;
		if (runningOnRaspberry) {
			Logger.info("Running on Pi.");
			pwmControllerConnector = new PCA9685Connector();
		} else {
			Logger.info("NOT running on Pi!");
			pwmControllerConnector = new PWMControllerConnectorDummy();
		}

		LightEnvironment env = createEnvironmentFromConfiguration(configuration, pwmControllerConnector);
		return env;
	}

	static LightEnvironment createEnvironmentFromConfiguration(LightEnvironmentConfiguration configuration, PWMControllerConnector pwmControllerConnector) {
		Objects.requireNonNull(configuration);
		Objects.requireNonNull(pwmControllerConnector);

		LightEnvironmentBuilder envBuilder = LightEnvironment.create(pwmControllerConnector);

		List<LightEnvironmentChannelConfiguration> channelConfig = configuration.getChannelConfig();
		if (channelConfig == null) {
			Logger.error("Light environment configuration has no channels configured.");
			return envBuilder.build();
		}

		if (channelConfig.isEmpty()) {
			Logger.error("Light environment configuration has no channels configured.");
			return envBuilder.build();
		}

		channelConfig.stream().map((chConf) -> {
			LightEnvironmentChannelBuilder channelBuilder = LightEnvironmentChannel.create(chConf.getId(), pwmControllerConnector).withName(chConf.getName())
					.withColor(chConf.getColor());
			chConf.getPins().forEach(channelBuilder::usePin);
			LightEnvironmentChannel channel = channelBuilder.build();
			return channel;
		}).forEach((channel) -> {
			envBuilder.withChannel(channel);
		});

		return envBuilder.build();
	}

	public static boolean isRunningOnRaspberry() {
		boolean runningOnRaspberry = false;

		try {
			String processor = SystemInfo.getProcessor();
			// TODO improve check for raspberry hardware
			if (processor.startsWith("ARM")) {
				runningOnRaspberry = true;
			}
		} catch (@SuppressWarnings("unused") Exception e) {
			// ignore, but we are sure we have no pi here
		}
		return runningOnRaspberry;
	}

	static class LightDaemonShutdownHook extends Thread {

		private LightServerController daemon;

		LightDaemonShutdownHook(LightServerController daemon) {
			this.daemon = daemon;
		}

		@Override
		public void run() {
			Logger.info("Recognized VM shutting down. Shutting down LightDaemon.");
			this.daemon.shutdown();
		}
	}
}
