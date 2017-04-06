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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.restexpress.Request;
import org.restexpress.Response;

import com.github.albahrani.aquacontrol.server.LightServerController;

import io.netty.handler.codec.http.HttpResponseStatus;

public class ServerLifecycleControllerTest {

	@Test
	public void testPause() {
		LightServerController daemon = mock(LightServerController.class);
		ServerLifecycleController controller = new ServerLifecycleController(daemon);

		Request request = mock(Request.class);
		Response response = mock(Response.class);
		controller.pause(request, response);

		verify(daemon).pause();
		verifyNoMoreInteractions(daemon);
		verify(response).setResponseStatus(HttpResponseStatus.OK);
	}

	@Test
	public void testPauseException() {
		LightServerController daemon = mock(LightServerController.class);
		doThrow(new NullPointerException("Test1")).when(daemon).pause();
		ServerLifecycleController controller = new ServerLifecycleController(daemon);

		Request request = mock(Request.class);
		Response response = mock(Response.class);
		controller.pause(request, response);

		verify(daemon).pause();
		verifyNoMoreInteractions(daemon);
		verify(response).setException(any(NullPointerException.class));
	}

	@Test
	public void testResume() {
		LightServerController daemon = mock(LightServerController.class);
		ServerLifecycleController controller = new ServerLifecycleController(daemon);

		Request request = mock(Request.class);
		Response response = mock(Response.class);
		controller.resume(request, response);

		verify(daemon).resume();
		verifyNoMoreInteractions(daemon);
		verify(response).setResponseStatus(HttpResponseStatus.OK);
	}

	@Test
	public void testResumeException() {
		LightServerController daemon = mock(LightServerController.class);
		doThrow(new NullPointerException("Test1")).when(daemon).resume();
		ServerLifecycleController controller = new ServerLifecycleController(daemon);

		Request request = mock(Request.class);
		Response response = mock(Response.class);
		controller.resume(request, response);

		verify(daemon).resume();
		verifyNoMoreInteractions(daemon);
		verify(response).setException(any(NullPointerException.class));
	}

	@Test
	public void testShutdown() {
		LightServerController daemon = mock(LightServerController.class);
		ServerLifecycleController controller = new ServerLifecycleController(daemon);

		Request request = mock(Request.class);
		Response response = mock(Response.class);
		controller.shutdown(request, response);

		verify(daemon).shutdown();
		verifyNoMoreInteractions(daemon);
		verify(response).setResponseStatus(HttpResponseStatus.OK);
	}

	@Test
	public void testShutdownException() {
		LightServerController daemon = mock(LightServerController.class);
		doThrow(new NullPointerException("Test1")).when(daemon).shutdown();
		ServerLifecycleController controller = new ServerLifecycleController(daemon);

		Request request = mock(Request.class);
		Response response = mock(Response.class);
		controller.shutdown(request, response);

		verify(daemon).shutdown();
		verifyNoMoreInteractions(daemon);
		verify(response).setException(any(NullPointerException.class));
	}

}
