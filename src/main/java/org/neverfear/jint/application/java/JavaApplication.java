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

import org.neverfear.jint.api.Handle;
import org.neverfear.jint.api.WaitStrategy;
import org.neverfear.jint.application.basic.BasicApplication;

public class JavaApplication
	extends BasicApplication {

	private final JavaDescription description;

	public JavaApplication(
			final JavaDescription description,
			final Handle handle,
			final WaitStrategy waitStrategy) {
		this(new ImmutableJavaDescription(description),
				handle,
				waitStrategy);
	}

	protected JavaApplication(
			final ImmutableJavaDescription description,
			final Handle handle,
			final WaitStrategy waitStrategy) {
		super(description,
				handle,
				waitStrategy);
		this.description = description;
	}

	@Override
	public JavaDescription description() {
		return this.description;
	}

}
