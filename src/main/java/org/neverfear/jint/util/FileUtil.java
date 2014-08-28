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

/**
 * @author doug@neverfear.org
 * 
 */
public final class FileUtil {

	private FileUtil() {
		throw new AssertionError();
	}

	/**
	 * Ensures that the passed directory exists, either by creating it or
	 * validating that any existing file is a directory.
	 * 
	 * @param directory
	 */
	public static void ensureDirectoryExists(final File directory) {
		if (directory.exists()) {
			if (!directory.isDirectory()) {
				throw new IllegalArgumentException("File already exists and is not a directory: " + directory);
			}
		} else if (directory.mkdirs()) {
			throw new IllegalStateException("Failed to create directory: " + directory);
		}
	}
}
