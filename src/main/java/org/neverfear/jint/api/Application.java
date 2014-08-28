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

public interface Application
	extends HandleCommon {

	/**
	 * Blocks until either the handle has started or the current thread is
	 * interrupted.
	 * 
	 * @throws InterruptedException
	 * @throws ApplicationException
	 */
	void awaitStart() throws InterruptedException, ApplicationException;

	/**
	 * Blocks until either the handle has stopped or the current thread is
	 * interrupted.
	 * 
	 * @throws InterruptedException
	 * @throws ApplicationException
	 */
	void awaitStop() throws InterruptedException, ApplicationException;

	/**
	 * Returns a description of settings used to create this application. Sub
	 * types are intended to specify a more specific {@link Description} object
	 * describing their specific natures.
	 * 
	 * @return the description object.
	 */
	@Nonnull
	Description description();
}
