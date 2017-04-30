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
