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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.albahrani.aquacontrol.server.config.LightServerConfigurationFactoryTest;
import com.github.albahrani.aquacontrol.server.json.JSONChannelTest;
import com.github.albahrani.aquacontrol.server.rest.ForcedValuesControllerTest;
import com.github.albahrani.aquacontrol.server.rest.PlanControllerTest;
import com.github.albahrani.aquacontrol.server.rest.RESTServerTest;
import com.github.albahrani.aquacontrol.server.rest.ServerLifecycleControllerTest;
import com.github.albahrani.aquacontrol.server.rest.StatusReportControllerTest;

@RunWith(Suite.class)
@SuiteClasses({ LightServerTest.class, LightServerControllerTest.class, RESTServerTest.class, LightServerConfigurationFactoryTest.class,
		ServerLifecycleControllerTest.class, ForcedValuesControllerTest.class, StatusReportControllerTest.class, PlanControllerTest.class,
		JSONChannelTest.class })
public class AllTests {
	// tests are configured via annotations
}
