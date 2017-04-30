package com.github.albahrani.aquacontrol.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
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
