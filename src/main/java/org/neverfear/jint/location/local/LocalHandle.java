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
package org.neverfear.jint.location.local;

import java.io.IOException;

import org.neverfear.jint.api.ApplicationException;
import org.neverfear.jint.api.Console;
import org.neverfear.jint.api.Description;
import org.neverfear.jint.api.Handle;
import org.neverfear.jint.api.Location;
import org.neverfear.jint.console.FilePipedConsole;
import org.neverfear.jint.util.ProcessUtil;
import org.neverfear.jint.util.RuntimeUtil;
import org.neverfear.jint.util.RuntimeUtil.ShutdownHook;

final class LocalHandle
	implements Handle {

	private final Location location;
	private final ProcessBuilder builder;

	private transient Process process = null;
	private transient boolean started = false;

	private transient FilePipedConsole console;

	private transient boolean hookSet = false;

	LocalHandle(final Location location, final Description description) {
		super();
		this.location = location;
		this.builder = createBuilder(description);
	}

	private static ProcessBuilder createBuilder(final Description description) {
		final ProcessBuilder builder = new ProcessBuilder().command(description.command())
				.directory(description.workingDirectory())
				.redirectErrorStream(description.isErrorMappedToOutput());

		if (description.isIOInherited()) {
			builder.inheritIO();
		}

		builder.environment()
				.putAll(description.environment());
		return builder;
	}

	private void initHook() {
		RuntimeUtil.addShutdownHook(new ShutdownHook() {

			@Override
			public void run() throws Exception {
				stop();
			}
		});
		this.hookSet = true;
	}

	@Override
	public void start() throws ApplicationException {
		if (!this.started) {
			try {
				if (!this.hookSet) {
					initHook();
				}
				this.process = this.builder.start();
				this.console = FilePipedConsole.fromProcess(this.process);
				this.started = true;
			} catch (final IOException e) {
				throw new ApplicationException(e);
			}
		}
	}

	@Override
	public void stop() throws ApplicationException {
		if (this.started) {
			this.process.destroy();
			this.started = false;
			try {
				this.console.close();
			} catch (final IOException e) {
				throw new ApplicationException(e);
			}
		}
	}

	private void ensureStartedAtLeastOnce() throws IllegalStateException {
		if (this.process == null) {
			throw new IllegalStateException("Never started");
		}
	}

	@Override
	public int exitCode() {
		ensureStartedAtLeastOnce();
		try {
			return this.process.exitValue();
		} catch (final IllegalThreadStateException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public boolean isRunning() {
		if (this.process == null) {
			return false;
		}

		if (!this.started) {
			return false;
		}

		return ProcessUtil.isAlive(this.process);
	}

	@Override
	public boolean isStarted() {
		return this.started;
	}

	@Override
	public Location location() {
		return this.location;
	}

	@Override
	public void await() throws InterruptedException {
		this.process.waitFor();
	}

	@Override
	public Console console() {
		ensureStartedAtLeastOnce();
		return this.console;
	}

	@Override
	public String toString() {
		return ProcessUtil.toCommand(this.builder.command());
	}

}
