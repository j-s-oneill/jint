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
package org.neverfear.jint.rule;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;

/**
 * The {@link ProcessRule} starts a basic process and stops it after the test
 * run.
 * 
 * @author doug@neverfear.org
 * 
 */
public class ProcessRule
	implements TestRule, Supplier<Process> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessRule.class);

	private final Thread hook = new Thread(new Runnable() {

		@Override
		public void run() {
			stop();
		}
	});

	private final ProcessBuilder processBuilder;
	private Process process = null;

	/**
	 * The fully qualified command line complete with quoting. This is loaded
	 * lazily and done so once to avoid long command lines taking a lot time to
	 * generate.
	 */
	private String commandLine = null;

	private ProcessRule(final List<String> command) {
		if (command.isEmpty()) {
			throw new IllegalArgumentException("Please supply a command");
		}
		this.processBuilder = new ProcessBuilder(command);

		if (LOGGER.isDebugEnabled()) {
			this.commandLine = toCommand(this.processBuilder.command());
		}
	}

	public ProcessRule env(final String key, final Object value) {
		if (key == null) {
			throw new IllegalArgumentException("null key");
		}
		if (value == null) {
			throw new IllegalArgumentException("null value");
		}

		this.processBuilder.environment()
				.put(key, value.toString());
		return this;
	}

	public ProcessRule workingDirectory(final String workingDirectory) {
		final File directory = new File(workingDirectory);
		if (!directory.exists()) {
			throw new IllegalArgumentException(workingDirectory + " does not exist");
		}

		this.processBuilder.directory(directory);
		return this;
	}

	public ProcessRule redirectStdErr() {
		this.processBuilder.redirectErrorStream(true);
		return this;
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		Runtime.getRuntime()
				.addShutdownHook(this.hook);
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				start();
				try {
					base.evaluate();
				} finally {
					stop();
				}

			}
		};
	}

	private static boolean containsWhitespace(final String string) {
		for (int index = 0; index < string.length(); index++) {
			final char ch = string.charAt(index);
			if (Character.isWhitespace(ch)) {
				return true;
			}
		}
		return false;
	}

	private static String toCommand(final List<String> command) {
		final StringBuilder builder = new StringBuilder();
		for (final String bit : command) {
			final boolean needQuotes = containsWhitespace(bit);
			if (needQuotes) {
				builder.append('"');
			}
			builder.append(bit);
			if (needQuotes) {
				builder.append('"');
			}
			builder.append(" ");
		}
		assert !command.isEmpty() : "Should have been caught during construction time";
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

	private void start() throws IOException {
		LOGGER.debug("Starting: {}", this.commandLine);

		this.process = this.processBuilder.start();

		LOGGER.debug("Started: {}", this.commandLine);
	}

	private void stop() {
		if (this.process != null) {
			LOGGER.debug("Stopping: {}", this.commandLine);

			this.process.destroy();
			this.process = null;

			LOGGER.debug("Stopped: {}", this.commandLine);
		}
	}

	@Override
	public Process get() {
		if (this.process == null) {
			throw new IllegalStateException("Process not set");
		}
		return this.process;
	}

	public static ProcessRule exec(final String command) {
		final StringTokenizer st = new StringTokenizer(command);
		final String[] cmdarray = new String[st.countTokens()];
		for (int i = 0; st.hasMoreTokens(); i++) {
			cmdarray[i] = st.nextToken();
		}
		return create(cmdarray);
	}

	public static ProcessRule create(final String... command) {
		return new ProcessRule(Arrays.asList(command));
	}
}
