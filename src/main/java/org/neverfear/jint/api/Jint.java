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
package org.neverfear.jint.api;

import org.neverfear.jint.application.basic.BasicApplicationBuilder;
import org.neverfear.jint.application.java.JavaApplicationBuilder;
import org.neverfear.jint.application.java.SystemPropertyBuilder;

public final class Jint {

	private Jint() {
		throw new AssertionError();
	}

	/**
	 * 
	 * @param mainClassName the name of the main-class
	 * @return a java application builder
	 */
	public static JavaApplicationBuilder java(final String mainClassName) {
		return new JavaApplicationBuilder().mainClassName(mainClassName);
	}

	/**
	 * Uses a class instance to build an application.
	 * 
	 * This method should generally be preferred over {@link #java(String)} to
	 * support easy refactoring where possible, given ClassLoaders and class
	 * paths available during the test.
	 * 
	 * @param mainClass the main-class
	 * @return a java application builder
	 */
	public static JavaApplicationBuilder java(final Class<?> mainClass) {
		return new JavaApplicationBuilder().mainClass(mainClass);
	}

	/**
	 * 
	 * @param executable
	 * @return a basic application builder
	 */
	public static BasicApplicationBuilder basic(final String executable) {
		return new BasicApplicationBuilder().executable(executable);
	}

	/**
	 * 
	 * @return a system property builder
	 */
	public static SystemPropertyBuilder sysProps() {
		return new SystemPropertyBuilder();
	}
}
