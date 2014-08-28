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
package org.neverfear.jint.application.java;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.neverfear.jint.application.basic.BasicDescription;

public interface JavaDescription
	extends BasicDescription {

	/**
	 * The canonical name of the main-class.
	 * 
	 * @return the main-class name.
	 */
	@Nonnull
	String mainClassName();

	/**
	 * If the {@link #mainClassName()} is on the current class path, you may use
	 * this method as a utility method to retrieve it.
	 * 
	 * @return the main-class if available.
	 * @throws ClassNotFoundException class is not on the current class path.
	 */
	@Nonnull
	Class<?> mainClass() throws ClassNotFoundException;

	/**
	 * 
	 * @return a non-null list of each path on the class path.
	 */
	@Nonnull
	List<String> classPath();

	/**
	 * The return value here does not include system properties specified in
	 * {@link #systemProperties()}.
	 * 
	 * @return a non-null list of JVM arguments to pass.
	 */
	@Nonnull
	List<String> jvmArguments();

	/**
	 * 
	 * @return a non-null map of system properties to pass.
	 */
	@Nonnull
	Map<String, String> systemProperties();
}
