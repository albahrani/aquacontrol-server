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

import com.github.albahrani.aquacontrol.logger.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.albahrani.aquacontrol.server.rest.RESTServer;

public class RESTServerTest {

    @BeforeClass
    public static void beforeClass() {
        Logger.setActive(false);
    }

    // TODO: 25.02.2020 test implementation fails on jitCI because it "failed to create a child event loop".
    //@Test
    public void testStartupShutdown() {
        RESTServer server = new RESTServer();
        server.start();
        server.shutdown();
    }

}
