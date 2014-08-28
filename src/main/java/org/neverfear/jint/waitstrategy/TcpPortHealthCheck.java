/*
 * Copyright 2014 doug@neverfear.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neverfear.jint.waitstrategy;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import org.neverfear.jint.api.Application;
import org.neverfear.jint.api.ApplicationException;

/**
 * @author doug@neverfear.org
 * 
 */
public final class TcpPortHealthCheck
	implements HealthCheck {

	private final String hostname;
	private final int port;

	public TcpPortHealthCheck(final int port) {
		this("localhost",
				port);
	}

	public TcpPortHealthCheck(final String hostname, final int port) {
		super();
		this.hostname = hostname;
		this.port = port;
	}

	@Override
	public boolean isHealthy(final Application application) throws ApplicationException {
		try (Socket socket = new Socket(this.hostname,
				this.port)) {
			/*
			 * Connection accepted.
			 */
			return true;
		} catch (final ConnectException e) {
			return false;
		} catch (final IOException e) {
			throw new ApplicationException(e);
		}
	}

}
