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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.restexpress.Request;
import org.restexpress.Response;

import com.github.albahrani.aquacontrol.server.LightServerController;
import com.github.albahrani.aquacontrol.server.json.JSONForceValue;
import com.github.albahrani.aquacontrol.server.rest.ForcedValuesController;

import io.netty.handler.codec.http.HttpResponseStatus;

public class ForcedValuesControllerTest {

	@Test
	public void testForceValue() {
		LightServerController daemon = mock(LightServerController.class);
		ForcedValuesController controller = new ForcedValuesController(daemon);
		
		Request request = mock(Request.class);
		when(request.getHeader(eq("channelId"), anyString())).thenReturn("warmwhite");
		JSONForceValue jsonForceValue = new JSONForceValue();
		jsonForceValue.setValue(75.0d);
		when(request.getBodyAs(JSONForceValue.class)).thenReturn(jsonForceValue);
		
		Response response = mock(Response.class);
		controller.forceValue(request, response);
		
		verify(daemon).setForcedValue("warmwhite", 75.0d);
		verifyNoMoreInteractions(daemon);
		verify(response).setResponseStatus(HttpResponseStatus.OK);
	}

	@Test
	public void testClearForcedValue() {
		LightServerController daemon = mock(LightServerController.class);
		ForcedValuesController controller = new ForcedValuesController(daemon);
		
		Request request = mock(Request.class);
		when(request.getHeader(eq("channelId"), anyString())).thenReturn("warmwhite");
		
		Response response = mock(Response.class);
		controller.clearForcedValue(request, response);
		
		verify(daemon).clearForcedValue("warmwhite");
		verifyNoMoreInteractions(daemon);
		verify(response).setResponseStatus(HttpResponseStatus.OK);
	}

}
