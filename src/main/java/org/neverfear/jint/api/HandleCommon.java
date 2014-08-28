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

import javax.annotation.Nonnull;

/**
 * Methods common to applications and handles. This is to avoid duplication
 * while avoiding {@link Application} extending {@link Handle}
 * 
 * @author doug@neverfear.org
 * 
 */
interface HandleCommon {

	/**
	 * The console provides access to standard output, error and input. Some
	 * handles may not be available to provide the console and so this may yield
	 * null. Generally speaking if the implementation of a handle supports the
	 * console then this will return non-null on every invocation.
	 * 
	 * @return the console object for this handle
	 * @throws UnsupportedOperationException if this handle does not support the
	 *         console.
	 */
	@Nonnull
	Console console();

	/**
	 * The location object for this handle. This represents where the handle is
	 * running and in which mode.
	 * 
	 * @return the location object.
	 */
	@Nonnull
	Location location();

	/**
	 * Start this handle.
	 * 
	 * @throws ApplicationException on start failure.
	 */
	void start() throws ApplicationException;

	/**
	 * Stop this handle.
	 * 
	 * @throws ApplicationException on stop failure.
	 */
	void stop() throws ApplicationException;

	/**
	 * The exit code of this handle. On Linux this is limited to between 0 and
	 * 255 (inclusive) but on Windows this range is larger.
	 * 
	 * @return the exit code.
	 * @throws IllegalStateException if the handle has not exited.
	 */
	int exitCode() throws IllegalStateException;

	/**
	 * May be used to determine if the handle is still running.
	 * 
	 * @return true if the handle is still running, false otherwise.
	 */
	boolean isRunning();

	/**
	 * May be used to determine if start or stop was last called.
	 * 
	 * @return true if the handle was started, false otherwise.
	 */
	boolean isStarted();

}
