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

import org.restexpress.Request;
import org.restexpress.Response;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;

public class CORSHelper {
    
    private CORSHelper(){
        //prevent instantiation
    }
    
	public static boolean handleCORS(Request request, Response response) {
		boolean continueProcessing = true;
		
    	String origin = request.getHeader("Origin");
    	if((origin == null) || ("".equals(origin))) {
    		origin = "*";
    	}
    	response.addHeader("Access-Control-Allow-Origin", origin);
        
        if (HttpMethod.OPTIONS.equals(request.getHttpMethod())) {
        	response.addHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
        	response.addHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
        	response.setResponseStatus(HttpResponseStatus.OK);
        	continueProcessing = false;
        }
		
		return continueProcessing;
	}
}
