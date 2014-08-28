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

import static java.lang.Thread.interrupted;
import static java.lang.Thread.yield;

import org.neverfear.jint.api.Application;
import org.neverfear.jint.api.ApplicationException;
import org.neverfear.jint.api.WaitStrategy;

public class UntilHealthyWaitStrategy
	implements WaitStrategy {

	private final HealthCheck healthCheck;

	public UntilHealthyWaitStrategy(final HealthCheck healthCheck) {
		super();
		this.healthCheck = healthCheck;
	}

	@Override
	public void waitFor(final Application application) throws InterruptedException, ApplicationException {
		while (!this.healthCheck.isHealthy(application)) {
			if (interrupted()) {
				throw new InterruptedException();
			}

			if (!application.isRunning()) {
				final int exitCode = application.exitCode();
				throw new ApplicationException("Application has exited with code " + exitCode);
			}

			yield();
		}
	}

}
