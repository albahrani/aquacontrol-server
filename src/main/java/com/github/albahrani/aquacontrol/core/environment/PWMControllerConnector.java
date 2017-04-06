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
package com.github.albahrani.aquacontrol.core.environment;

import java.util.Set;

import com.pi4j.io.gpio.Pin;

public interface PWMControllerConnector {

	public abstract void setPwmValue(Set<Pin> pinList, int duration);

	public abstract void shutdown();

	public abstract void provisionPwmOutputPin(Pin pin);

}