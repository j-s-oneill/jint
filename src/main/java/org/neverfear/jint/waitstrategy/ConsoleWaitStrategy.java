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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.neverfear.jint.api.Application;
import org.neverfear.jint.api.ApplicationException;
import org.neverfear.jint.api.Console;
import org.neverfear.jint.api.WaitStrategy;

/**
 * This implementation consumes stdout until the pattern is matched.
 * 
 * @author doug@neverfear.org
 * 
 */
public class ConsoleWaitStrategy
	implements WaitStrategy {

	private final Pattern pattern;

	public ConsoleWaitStrategy(final String regex) {
		this(Pattern.compile(regex));
	}

	public ConsoleWaitStrategy(final Pattern pattern) {
		super();
		this.pattern = pattern;
	}

	@Override
	public void waitFor(final Application application) throws InterruptedException, ApplicationException {
		String line = null;

		final Console console = application.console();
		try (InputStream standardOutput = console.output()) {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(standardOutput));
			while ((line = reader.readLine()) != null) {

				if (!application.isRunning()) {
					final int exitCode = application.exitCode();
					throw new ApplicationException("Application has exited with code " + exitCode);
				}

				final Matcher matcher = this.pattern.matcher(line);
				if (matcher.find()) {
					return;
				}

				if (interrupted()) {
					throw new InterruptedException();
				}
			}
		} catch (final IOException e) {
			throw new ApplicationException("Failed to start successfully",
					e);
		}

		throw new ApplicationException("Failed to start successfully");
	}
}
