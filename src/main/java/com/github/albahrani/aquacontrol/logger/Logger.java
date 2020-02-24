/**
 * Copyright © 2017 albahrani (https://github.com/albahrani)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.albahrani.aquacontrol.logger;

import io.netty.handler.codec.http.HttpMethod;

public class Logger {

    private static boolean active = true;

    public static void setActive(boolean isActive) {
        Logger.active = isActive;
    }

    public static void warn(String s, Object... objects) {
        if (!Logger.active) {
            return;
        }
        org.pmw.tinylog.Logger.warn(s, objects);
    }

    public static void error(String s) {
        if (!Logger.active) {
            return;
        }
        org.pmw.tinylog.Logger.error(s);
    }

    public static void error(Exception e, String s) {
        if (!Logger.active) {
            return;
        }
        org.pmw.tinylog.Logger.error(e, s);
    }

    public static void error(Exception e, String s, Object... objects) {
        if (!Logger.active) {
            return;
        }
        org.pmw.tinylog.Logger.error(e, s, objects);
    }

    public static void info(String s, Object... objects) {
        if (!Logger.active) {
            return;
        }
        org.pmw.tinylog.Logger.info(s, objects);
    }
}
