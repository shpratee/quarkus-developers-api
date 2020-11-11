/*
 * Copyright 2020 BASE Logic, Inc
 * https://baselogic.io
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
package com.fiserv.api.ipp.documgmt.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import io.vertx.core.http.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter to log request and response elements
 *
 * @author prateek.sharma
 */
@Provider
public class RequestResponseLoggingFilter implements ContainerRequestFilter {

	@Context
	UriInfo info;

	@Context
	HttpServerRequest request;

	private static Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class.getName());

	@Override
	public void filter(ContainerRequestContext context) throws IOException {
		final String method = context.getMethod();
		final String path = info.getPath();
		final String address = request.remoteAddress().toString();

		logger.info(String.format("Request received %s %s from IP %s", method, path, address));
	}
}
