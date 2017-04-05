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
package com.github.albahrani.aquacontrol.server.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.StringWriter;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.restexpress.Request;
import org.restexpress.Response;

import com.github.albahrani.aquacontrol.server.LightServerController;
import com.github.albahrani.aquacontrol.server.json.JSONChannel;
import com.github.albahrani.aquacontrol.server.json.JSONPlan;
import com.github.albahrani.aquacontrol.server.json.JSONTimeValuePair;

import io.netty.handler.codec.http.HttpResponseStatus;

public class PlanControllerTest {

	@Test
	public void test() {
		LightServerController daemon = mock(LightServerController.class);
		File planFile = new File("planFile");
		when(daemon.getLightPlanFile()).thenReturn(planFile);
		PlanController controller = new PlanController(daemon);
		Request request = mock(Request.class);
		JSONPlan jsonPlan = new JSONPlan();
		when(request.getBodyAs(JSONPlan.class)).thenReturn(jsonPlan);

		Response response = mock(Response.class);
		controller.upload(request, response);

		verify(daemon).setLightPlan(any(), any());
		verify(daemon).getLightPlanFile();
		verifyNoMoreInteractions(daemon);
		verify(response).setResponseStatus(HttpResponseStatus.OK);
	}

	@Test
	public void testPlanStore() {
		LightServerController daemon = mock(LightServerController.class);
		StringWriter writer = new StringWriter();

		JSONPlan plan = new JSONPlan();
		List<JSONChannel> channels = new ArrayList<>();
		JSONChannel channel = new JSONChannel();
		channel.setId("0x20");

		List<JSONTimeValuePair> timetable = new ArrayList<>();
		JSONTimeValuePair p1 = new JSONTimeValuePair();
		p1.setTime(LocalTime.of(6, 0));
		p1.setPerc(0.0d);
		timetable.add(p1);
		JSONTimeValuePair p2 = new JSONTimeValuePair();
		p2.setTime(LocalTime.of(8, 0));
		p2.setPerc(100.0d);
		timetable.add(p2);
		channel.setTimetable(timetable);
		channels.add(channel);
		plan.setChannels(channels);

		PlanController controller = new PlanController(daemon);
		controller.write(plan, writer);

		String planStr = writer.toString();

		String rn = System.getProperty("line.separator");

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(rn);
		sb.append("  \"channels\" : [ {");
		sb.append(rn);
		sb.append("    \"id\" : \"0x20\",");
		sb.append(rn);
		sb.append("    \"timetable\" : [ {");
		sb.append(rn);
		sb.append("      \"time\" : [ 6, 0 ],");
		sb.append(rn);
		sb.append("      \"perc\" : 0.0");
		sb.append(rn);
		sb.append("    }, {");
		sb.append(rn);
		sb.append("      \"time\" : [ 8, 0 ],");
		sb.append(rn);
		sb.append("      \"perc\" : 100.0");
		sb.append(rn);
		sb.append("    } ]");
		sb.append(rn);
		sb.append("  } ]");
		sb.append(rn);
		sb.append("}");

		assertEquals("Unexpected LightPlan.", sb.toString(), planStr);
	}
}
