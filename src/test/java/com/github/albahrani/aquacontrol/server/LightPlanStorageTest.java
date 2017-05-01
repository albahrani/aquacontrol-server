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
package com.github.albahrani.aquacontrol.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.github.albahrani.aquacontrol.server.json.JSONChannel;
import com.github.albahrani.aquacontrol.server.json.JSONPlan;
import com.github.albahrani.aquacontrol.server.json.JSONTimeValuePair;
import com.github.albahrani.dimmingplan.DimmingPlan;
import com.github.albahrani.dimmingplan.DimmingPlanChannel;

public class LightPlanStorageTest {

	@Test
	public void testFromJson() {

		JSONPlan jsonPlan = new JSONPlan();
		List<JSONChannel> jsonChannels = new ArrayList<>();
		JSONChannel jsonChannel = new JSONChannel();
		jsonChannel.setId("0x20");

		List<JSONTimeValuePair> jsonTimetable = new ArrayList<>();
		JSONTimeValuePair p1 = new JSONTimeValuePair();
		p1.setTime(LocalTime.of(6, 0));
		p1.setPerc(0.0d);
		jsonTimetable.add(p1);
		JSONTimeValuePair p2 = new JSONTimeValuePair();
		p2.setTime(LocalTime.of(8, 0));
		p2.setPerc(100.0d);
		jsonTimetable.add(p2);
		jsonChannel.setTimetable(jsonTimetable);
		jsonChannels.add(jsonChannel);
		jsonPlan.setChannels(jsonChannels);

		DimmingPlan plan = LightPlanStorage.fromJSON(jsonPlan);

		assertNotNull(plan);
		assertEquals(1, plan.getChannelAmount());
		Set<String> channelNames = plan.getChannelNames();
		assertNotNull(channelNames);
		assertTrue(channelNames.contains("0x20"));
		DimmingPlanChannel channel = plan.channel("0x20");
		assertNotNull(channel);
		assertThat(channel.getPercentage(LocalTime.of(6, 0))).hasValueCloseTo(0.0d, within(0.001d));
		assertThat(channel.getPercentage(LocalTime.of(8, 0))).hasValueCloseTo(100.0d, within(0.001d));
	}

	@Test
	public void testPlanStore() {
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

		LightPlanStorage controller = new LightPlanStorage();
		boolean success = controller.write(plan, writer);
		assertTrue(success);
		
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

	@Test
	public void testPlanLoad() {
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
		String testString = sb.toString();

		InputStream stream = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
		LightPlanStorage controller = new LightPlanStorage();
		JSONPlan jsonPlan;
		try {
			jsonPlan = controller.read(stream);
		} catch (IOException e) {
			fail("got unexpected IOException while loading plan.");
			return;
		}
		assertNotNull(jsonPlan);
		List<JSONChannel> channels = jsonPlan.getChannels();
		assertNotNull(channels);
		assertEquals(1, channels.size());
		JSONChannel channel = channels.get(0);
		assertNotNull(channel);
		assertEquals("0x20", channel.getId());
		List<JSONTimeValuePair> timetable = channel.getTimetable();
		assertNotNull(timetable);
		assertEquals(2, timetable.size());
		JSONTimeValuePair tte1 = timetable.get(0);
		assertEquals(LocalTime.of(6, 0), tte1.getTime());
		assertEquals(0.0d, tte1.getPerc(), 0.001d);
		JSONTimeValuePair tte2 = timetable.get(1);
		assertEquals(LocalTime.of(8, 0), tte2.getTime());
		assertEquals(100.0d, tte2.getPerc(), 0.001d);
	}
	
}
