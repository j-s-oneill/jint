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
package org.neverfear.jint.application.basic;

import static org.slf4j.LoggerFactory.getLogger;

import org.neverfear.jint.api.Application;
import org.neverfear.jint.api.ApplicationException;
import org.neverfear.jint.api.Console;
import org.neverfear.jint.api.Handle;
import org.neverfear.jint.api.Location;
import org.neverfear.jint.api.WaitStrategy;
import org.neverfear.jint.util.ProcessUtil;
import org.slf4j.Logger;

/**
 * <p>
 * Represents the most basic application in the system. This is one that offers
 * no extra features than any ordinary application you might start on the
 * command line.
 * </p>
 * 
 * <p>
 * This class has been designed to be extended by more specialised applications.
 * </p>
 * 
 * @author doug@neverfear.org
 * 
 */
public class BasicApplication
	implements Application {

	private final Logger logger = getLogger(getClass());

	private final BasicDescription description;
	private final Handle handle;
	private final WaitStrategy waitStrategy;

	public BasicApplication(
			final BasicDescription description,
			final Handle handle,
			final WaitStrategy waitStrategy) {
		this(new ImmutableBasicDescription(description),
				handle,
				waitStrategy);
	}

	protected BasicApplication(
			final ImmutableBasicDescription description,
			final Handle handle,
			final WaitStrategy waitStrategy) {
		this.description = description;
		this.handle = handle;
		this.waitStrategy = waitStrategy;
	}

	@Override
	public BasicDescription description() {
		return this.description;
	}

	@Override
	public String toString() {
		return ProcessUtil.toCommand(this.description.command());
	}

	/**
	 * 
	 * @param format May contain "{}" which will be expanded to include the {
	 *        {@link #toString()} of this object.
	 */
	private void logStateChange(final String format) {
		this.logger.trace(format, this);
	}

	@Override
	public void start() throws ApplicationException {
		logStateChange("Starting {}");
		this.handle.start();
		logStateChange("Start initiated {}");
	}

	@Override
	public void stop() throws ApplicationException {
		logStateChange("Stopping {}");
		this.handle.stop();
		logStateChange("Stop initiated for {}");
	}

	@Override
	public void awaitStart() throws InterruptedException, ApplicationException {
		logStateChange("Awaiting start of {}");
		this.waitStrategy.waitFor(this);
		logStateChange("Started {}");
	}

	@Override
	public void awaitStop() throws InterruptedException, ApplicationException {
		if (this.handle.isRunning()) {
			logStateChange("Awaiting stop of {}");
			this.handle.await();
			logStateChange("Stopped {}");
		}
	}

	@Override
	public int exitCode() {
		return this.handle.exitCode();
	}

	@Override
	public boolean isRunning() {
		return this.handle.isRunning();
	}

	@Override
	public boolean isStarted() {
		return this.handle.isStarted();
	}

	@Override
	public Location location() {
		return this.handle.location();
	}

	@Override
	public Console console() {
		return this.handle.console();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
		result = prime * result + ((this.handle == null) ? 0 : this.handle.hashCode());
		result = prime * result + ((this.waitStrategy == null) ? 0 : this.waitStrategy.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final BasicApplication other = (BasicApplication) obj;
		if (this.description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!this.description.equals(other.description)) {
			return false;
		}
		if (this.handle == null) {
			if (other.handle != null) {
				return false;
			}
		} else if (!this.handle.equals(other.handle)) {
			return false;
		}
		if (this.waitStrategy == null) {
			if (other.waitStrategy != null) {
				return false;
			}
		} else if (!this.waitStrategy.equals(other.waitStrategy)) {
			return false;
		}
		return true;
	}

}
