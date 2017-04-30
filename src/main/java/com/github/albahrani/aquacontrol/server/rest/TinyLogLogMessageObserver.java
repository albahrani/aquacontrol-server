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

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.pmw.tinylog.Logger;
import org.restexpress.Request;
import org.restexpress.Response;
import org.restexpress.pipeline.MessageObserver;

public class TinyLogLogMessageObserver extends MessageObserver {
	private Map<String, Long> timers = new ConcurrentHashMap<>();

	@Override
	protected void onReceived(Request request, Response response) {
		timers.put(request.getCorrelationId(), System.currentTimeMillis());
		Logger.info("{} {} {} received.", request.getCorrelationId(), request.getEffectiveHttpMethod(), request.getUrl());
	}

	@Override
	protected void onException(Throwable exception, Request request, Response response) {
		Logger.info("{} {} {} threw exception: {}", request.getCorrelationId(), request.getEffectiveHttpMethod(), request.getUrl(), exception.getMessage());
	}

	@Override
	protected void onSuccess(Request request, Response response) {
		Logger.info("{} {} {} was successfull.", request.getCorrelationId(), request.getEffectiveHttpMethod(), request.getUrl());
	}

	@Override
	protected void onComplete(Request request, Response response) {
		Long startTime = timers.remove(request.getCorrelationId());
		Optional<String> duration = Optional.empty();
		if (startTime != null) {
			duration = Optional.of(String.valueOf(System.currentTimeMillis() - startTime) + "ms");
		}

		Logger.info("{} {} {} responded with {} in {}", request.getCorrelationId(), request.getEffectiveHttpMethod(), request.getUrl(),
				response.getResponseStatus(),
				duration.orElse("(no timer found)"));
	}
}
