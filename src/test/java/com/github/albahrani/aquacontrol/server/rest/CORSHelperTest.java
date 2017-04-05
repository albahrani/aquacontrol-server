package com.github.albahrani.aquacontrol.server.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.restexpress.Request;
import org.restexpress.Response;

import io.netty.handler.codec.http.HttpMethod;

public class CORSHelperTest {

	@Test
	public void testGet() {
		Request request = mock(Request.class);
		when(request.getHttpMethod()).thenReturn(HttpMethod.GET);
		Response response = new Response();
		boolean continueProcessing = CORSHelper.handleCORS(request, response);
		assertTrue(continueProcessing);
		assertEquals("*", response.getHeader("Access-Control-Allow-Origin"));
		assertNull(response.getHeader("Access-Control-Allow-Methods"));
	}

	@Test
	public void testOptions() {
		Request request = mock(Request.class);
		when(request.getHeader("Origin")).thenReturn("123.123.123.123");
		when(request.getHttpMethod()).thenReturn(HttpMethod.OPTIONS);
		Response response = new Response();
		boolean continueProcessing = CORSHelper.handleCORS(request, response);
		assertFalse(continueProcessing);
		assertEquals("123.123.123.123", response.getHeader("Access-Control-Allow-Origin"));
		assertEquals("POST, GET, PUT, DELETE, OPTIONS", response.getHeader("Access-Control-Allow-Methods"));
		assertEquals("Content-Type, Accept", response.getHeader("Access-Control-Allow-Headers"));
	}

}
