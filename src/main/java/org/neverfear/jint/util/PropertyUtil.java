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
import static org.neverfear.jint.util.RuntimeUtil.classPath;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import com.google.common.base.Joiner;

public final class PropertyUtil {

	public static final Joiner CLASSPATH_JOINER = Joiner.on(pathSeparator);
	public static final Joiner LIBRARYPATH_JOINER = Joiner.on(pathSeparator);
	private static final Map<String, String> ENVIRONMENT = Collections.unmodifiableMap(System.getenv());

	/**
	 * User's current working directory
	 */
	private static final String WORKING_DIRECTORY_PROPERTY = "user.dir";

	/**
	 * Default temporary file path
	 */
	private static final String TEMPDIR_PROPERTY = "java.io.tmpdir";

	private PropertyUtil() {
		throw new AssertionError();
	}

	public static String classPathString() {
		return CLASSPATH_JOINER
				.join(classPath());
	}

	public static File currentWorkingDirectory() {
		final String workingDirectory = System.getProperty(WORKING_DIRECTORY_PROPERTY);
		assert workingDirectory != null;
		return new File(workingDirectory);
	}

	public static Map<String, String> currentEnvironment() {
		return ENVIRONMENT;
	}

	public static File tempDirectory() {
		final String tempDirectoryName = System.getProperty(TEMPDIR_PROPERTY);
		assert tempDirectoryName != null;
		return new File(tempDirectoryName);
	}

}
