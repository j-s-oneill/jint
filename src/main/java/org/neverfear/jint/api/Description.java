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

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Describes how to start a process application.
 * 
 * @author doug@neverfear.org
 * 
 */
public interface Description {

	/**
	 * The working directory of the application.
	 * 
	 * @return a file representation of the working directory of the
	 *         application.
	 */
	@Nonnull
	File workingDirectory();

	/**
	 * The environment the application will have initially.
	 * 
	 * @return the environment.
	 */
	@Nonnull
	Map<String, String> environment();

	/**
	 * The full command with every component from the executable to the
	 * arguments. e.g. "ls -l --color" is the same logically equal to a
	 * List<String>("ls", "-l", "--color").
	 * 
	 * @return list of each component.
	 */
	@Nonnull
	List<String> command();

	/**
	 * 
	 * @return true if standard error is mapped to standard output.
	 */
	boolean isErrorMappedToOutput();

	/**
	 * 
	 * @return
	 */
	boolean isIOInherited();

}