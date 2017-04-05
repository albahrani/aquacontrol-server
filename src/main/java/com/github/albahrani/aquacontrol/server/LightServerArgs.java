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
package com.github.albahrani.aquacontrol.server;

import java.io.File;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.pmw.tinylog.Logger;

public class LightServerArgs {

	private CmdLineParser parser = new CmdLineParser(this);

	@Option(name = "-c", aliases = { "--config" }, required = true, usage = "configuration file of the daemon")
	private File configFile;

	@Option(name = "-p", aliases = {
			"--plan" }, required = true, usage = "file used to read/write the file plan from/to. You need write permission to this file!")
	private File lightPlanFile;

	@Option(name = "-h", aliases = { "--help" }, usage = "shows this help", help = true)
	private boolean showUsage = false;

	public boolean parse(String[] args) {
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			Logger.error(e, "Invalid arguments for lightdaemon.");
			System.err.println(e.getMessage());
			System.err.println("java -jar lightdaemon.jar [options...] arguments...");
			parser.printUsage(System.err);
			return false;
		}
		return true;
	}

	public File getConfigFile() {
		return configFile;
	}

	public File getLightPlanFile() {
		return lightPlanFile;
	}

	public void setConfigFile(File configPath) {
		this.configFile = configPath;
	}

	public void setLightPlanFile(File lightPlanPath) {
		this.lightPlanFile = lightPlanPath;
	}

	public void setShowUsage(boolean showUsage) {
		this.showUsage = showUsage;
	}

	public boolean isShowUsage() {
		return this.showUsage;
	}

	public void showUsage() {
		this.parser.printUsage(System.out);
	}

}
