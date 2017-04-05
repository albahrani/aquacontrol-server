package com.github.albahrani.aquacontrol.server.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONChannelTest {

	@Test
	public void testToString() {
		JSONChannel json = new JSONChannel();
		json.setId("2");
		List<JSONTimeValuePair> timetable = new ArrayList<>();
		JSONTimeValuePair tte = new JSONTimeValuePair();
		tte.setTime(LocalTime.of(6, 30));
		tte.setPerc(12.0d);
		timetable.add(tte);

		JSONTimeValuePair tte2 = new JSONTimeValuePair();
		tte2.setTime(LocalTime.of(12, 00));
		tte2.setPerc(56.0d);
		timetable.add(tte2);

		json.setTimetable(timetable);

		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonString = mapper.writeValueAsString(json);
			assertNotNull(jsonString);
			assertEquals("{\"id\":\"2\",\"timetable\":[{\"time\":[6,30],\"perc\":12.0},{\"time\":[12,0],\"perc\":56.0}]}", jsonString);
		} catch (@SuppressWarnings("unused") JsonProcessingException e) {
			fail("Error while converting JSONChannel to String.");
		}
	}

	@Test
	public void testToJson() {

		String jsonString = "{\"id\":\"2\",\"timetable\":[{\"time\":[6,30],\"perc\":12.0},{\"time\":[12,0],\"perc\":56.0}]}";

		ObjectMapper mapper = new ObjectMapper();
		try {
			JSONChannel json = mapper.readValue(jsonString, JSONChannel.class);
			assertNotNull(json);
			assertEquals("2", json.getId());
			List<JSONTimeValuePair> timetable = json.getTimetable();
			assertNotNull(timetable);
			assertEquals(2, timetable.size());
			JSONTimeValuePair ttp = timetable.get(0);
			assertNotNull(ttp);
			assertEquals(LocalTime.of(6, 30), ttp.getTime());
			assertEquals(12, ttp.getPerc(), 0.0001);
			JSONTimeValuePair ttp2 = timetable.get(1);
			assertNotNull(ttp2);
			assertEquals(LocalTime.of(12, 00), ttp2.getTime());
			assertEquals(56, ttp2.getPerc(), 0.0001);
		} catch (IOException e) {
			fail("Error while converting String to JSONChannel." + e.getMessage());
		}
	}

}
