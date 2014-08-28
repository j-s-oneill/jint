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
package org.neverfear.jint.util;

import static java.io.File.pathSeparator;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.lang.management.ManagementFactory;
import java.util.List;

public final class RuntimeUtil {

	/**
	 * The system property that contains the classpath for this JVM
	 */
	private static final String JAVA_CLASS_PATH_PROPERTY = "java.class.path";

	private RuntimeUtil() {
		throw new AssertionError();
	}

	/**
	 * 
	 * @return true if this JVM was launched with Java debug options, false
	 *         otherwise.
	 */
	public static boolean isDebug() {
		final List<String> arguments = ManagementFactory.getRuntimeMXBean()
				.getInputArguments();

		return argumentsContainsDebugOptions(arguments);
	}

	/**
	 * 
	 * @return true if the arguments contain Java debug options, false
	 *         otherwise.
	 */
	public static boolean argumentsContainsDebugOptions(final List<String> arguments) {
		for (final String argument : arguments) {
			if (argument.startsWith("-agentlib:jdwp")) {
				return true;
			} else if (argument.startsWith("-Xrunjdwp:")) {
				return true;
			} else if ("-Xdebug".equals(argument)) {
				return true;
			}
		}
		return false;
	}

	public static List<String> classPath() {
		final String[] classpath = System.getProperty(JAVA_CLASS_PATH_PROPERTY)
				.split(pathSeparator);
		return unmodifiableList(asList(classpath));
	}

	/**
	 * Differs from {@link Runtime#addShutdownHook(Thread)} only in that you can
	 * specify a {@link Runnable} instead of a {@link Thread}
	 * 
	 * @param runnable
	 */
	public static void addShutdownHook(final Runnable runnable) {
		Runtime.getRuntime()
				.addShutdownHook(new Thread(runnable));
	}

	/**
	 * Adds a shutdown hook triggered when the JVM exits.
	 * 
	 * @param hook
	 */
	public static void addShutdownHook(final ShutdownHook hook) {
		Runtime.getRuntime()
				.addShutdownHook(new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							hook.run();
						} catch (final Exception ignored) {
						}
					}
				}));
	}

	/**
	 * Convenience interface for actions to be taken on shutdown for which any
	 * exception or error do not matter.
	 */
	public interface ShutdownHook {

		void run() throws Exception;
	}
}
