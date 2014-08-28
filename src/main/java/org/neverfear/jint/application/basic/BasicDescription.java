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
package org.neverfear.jint.application.basic;

import java.util.List;

import javax.annotation.Nonnull;

import org.neverfear.jint.api.Description;

public interface BasicDescription
	extends Description {

	/**
	 * The executable.
	 * 
	 * @return A non-null executable.
	 */
	@Nonnull
	String executable();

	/**
	 * Arguments to be passed to the executable. Index 0 is the first argument.
	 * 
	 * @return A non-null list of arguments.
	 */
	@Nonnull
	List<String> arguments();
}
