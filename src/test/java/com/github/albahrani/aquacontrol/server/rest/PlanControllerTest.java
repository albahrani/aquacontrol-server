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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.restexpress.Request;
import org.restexpress.Response;

import com.github.albahrani.aquacontrol.server.LightServerController;
import com.github.albahrani.aquacontrol.server.json.JSONPlan;

import io.netty.handler.codec.http.HttpResponseStatus;

public class PlanControllerTest {

	@Test
	public void testUpload() {
		LightServerController daemon = mock(LightServerController.class);
		PlanController controller = new PlanController(daemon);
		Request request = mock(Request.class);
		JSONPlan jsonPlan = new JSONPlan();
		when(request.getBodyAs(JSONPlan.class)).thenReturn(jsonPlan);

		Response response = mock(Response.class);
		controller.upload(request, response);

		verify(daemon).updateLightPlan(jsonPlan);
		verifyNoMoreInteractions(daemon);
		verify(response).setResponseStatus(HttpResponseStatus.OK);
	}

	@Test
	public void testGet() {
		LightServerController daemon = mock(LightServerController.class);
		JSONPlan jsonPlan = new JSONPlan();
		when(daemon.getJsonLightPlan()).thenReturn(jsonPlan);

		Request request = mock(Request.class);
		Response response = new Response();

		PlanController controller = new PlanController(daemon);
		controller.get(request, response);

		assertEquals(HttpResponseStatus.OK, response.getResponseStatus());
		Object responseBody = response.getBody();
		assertNotNull(responseBody);
		assertTrue(responseBody instanceof JSONPlan);
		JSONPlan responseJsonPlan = (JSONPlan) responseBody;
		assertEquals(jsonPlan, responseJsonPlan);
	}
}
