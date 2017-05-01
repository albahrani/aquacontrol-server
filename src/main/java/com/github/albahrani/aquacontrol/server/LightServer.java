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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.pmw.tinylog.Logger;

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
import com.pi4j.system.SystemInfo;

/**
 * Hello world!
 *
 */
public class LightServer {

	private LightServer() {
		// prevent instantiation
	}

	public static void main(String[] args) {
		
		PWMControllerConnector pwmControllerConnector;
		if (isRunningOnRaspberry()) {
			Logger.info("Running on Pi.");
			pwmControllerConnector = new PCA9685Connector();
		} else {
			Logger.info("NOT running on Pi!");
			pwmControllerConnector = new PWMControllerConnectorDummy();
		}
		
		Optional<LightServerController> optionalServer = initLightServer(args, pwmControllerConnector);
		optionalServer.ifPresent(LightServerController::start);
	}

	static Optional<LightServerController> initLightServer(String[] args, PWMControllerConnector pwmControllerConnector) {
		Optional<LightServerArgs> optionalCliArguments = parseArgs(args);
		if (!optionalCliArguments.isPresent()) {
			return Optional.empty();
		}
		LightServerArgs cliArguments = optionalCliArguments.get();

		LightEnvironmentConfiguration configuration = new LightEnvironmentConfiguration();
		try {
			configuration = LightServerConfigurationFactory.loadConfiguration(cliArguments.getConfigFile());
		} catch (InvalidConfigurationException e) {
			Logger.error(e, "Configuration could not be loaded from '{}'. Not starting application.",
					cliArguments.getConfigFile());
			return Optional.empty();
		}

		LightServerController daemon = new LightServerController();
		Runtime.getRuntime().addShutdownHook(new LightDaemonShutdownHook(daemon));

		daemon.setLightPlanStorage(new LightPlanStorage());
		daemon.loadLightPlanFromFile(cliArguments.getLightPlanFile());

		LightEnvironment env = initLightEnvironemnt(configuration, pwmControllerConnector);
		daemon.setLightEnvironment(env);

		return Optional.of(daemon);
	}

	static Optional<LightServerArgs> parseArgs(String[] args) {
		LightServerArgs cliArguments = new LightServerArgs();
		boolean parse = cliArguments.parse(args);
		if (!parse) {
			Logger.error("Error while parsing cli arguments. Not starting application.");
			return Optional.empty();
		}

		if (cliArguments.isShowUsage()) {
			cliArguments.showUsage();
			Logger.info("Usage information is shown. Not starting application.");
			return Optional.empty();
		}

		return Optional.of(cliArguments);
	}

	private static LightEnvironment initLightEnvironemnt(LightEnvironmentConfiguration configuration, PWMControllerConnector pwmControllerConnector) {
		Objects.requireNonNull(configuration);
		return createEnvironmentFromConfiguration(configuration, pwmControllerConnector);
	}

	static LightEnvironment createEnvironmentFromConfiguration(LightEnvironmentConfiguration configuration,
			PWMControllerConnector pwmControllerConnector) {
		Objects.requireNonNull(configuration);
		Objects.requireNonNull(pwmControllerConnector);

		LightEnvironmentBuilder envBuilder = LightEnvironment.create(pwmControllerConnector);

		List<LightEnvironmentChannelConfiguration> channelConfig = configuration.getChannelConfig();
		if (channelConfig == null) {
			Logger.warn("Light environment configuration has no channels configured.");
			return envBuilder.build();
		}

		if (channelConfig.isEmpty()) {
			Logger.warn("Light environment configuration has no channels configured.");
			return envBuilder.build();
		}

		channelConfig.stream().map(chConf -> {
			LightEnvironmentChannelBuilder channelBuilder = LightEnvironmentChannel
					.create(chConf.getId(), pwmControllerConnector).withName(chConf.getName())
					.withColor(chConf.getColor());
			chConf.getPins().forEach(channelBuilder::usePin);
			return channelBuilder.build();
		}).forEach(envBuilder::withChannel);

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
		} catch (Exception e) {
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
