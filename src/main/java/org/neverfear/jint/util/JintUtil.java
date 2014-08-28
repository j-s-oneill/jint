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

import java.io.File;
import java.io.IOException;

public final class JintUtil {

	public static final String PROPERTY_TEMP_DIRECTORY = "jint.tempdir";

	private static final String DEFAULT_TEMP_DIRECTORY_NAME = "jint";

	private JintUtil() {
		throw new AssertionError();
	}

	public static File createTempFile(final Class<?> owner) throws IOException {
		return File.createTempFile(owner.getCanonicalName(), null, tempDirectory());
	}

	public static File createTempFile(final Class<?> owner, final String name) throws IOException {
		final String suffix;
		if (name == null || name.trim()
				.length() == 0) {
			suffix = null;
		} else {
			suffix = "." + name;
		}
		final String prefix = owner.getCanonicalName() + "-";
		return File.createTempFile(prefix, suffix, tempDirectory());
	}

	/**
	 * May be specified using the system property
	 * {@link #PROPERTY_TEMP_DIRECTORY}. If absent will create & use a sub
	 * directory under "java.io.tmpdir" called "jint". e.g. /tmp/jint
	 * 
	 * @return
	 */
	public static File tempDirectory() {
		final String userSpecifiedTempDirectory = System.getProperty(PROPERTY_TEMP_DIRECTORY);

		final File jintTempDirectory;
		if (userSpecifiedTempDirectory != null) {
			jintTempDirectory = new File(userSpecifiedTempDirectory);
		} else {
			final File systemTempDirectory = PropertyUtil.tempDirectory();
			jintTempDirectory = new File(systemTempDirectory,
					DEFAULT_TEMP_DIRECTORY_NAME);
		}

		if (!jintTempDirectory.exists()) {
			jintTempDirectory.mkdir();
		}
		return jintTempDirectory;
	}

}
