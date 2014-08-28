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
package org.neverfear.jint.waitstrategy;

import org.neverfear.jint.api.Application;
import org.neverfear.jint.api.WaitStrategy;

/**
 * This strategy does not wait.
 */
public final class NoOpWaitStrategy
	implements WaitStrategy {

	public static final NoOpWaitStrategy INSTANCE = new NoOpWaitStrategy();

	private NoOpWaitStrategy() {}

	@Override
	public void waitFor(final Application application) {
		/*
		 * This method is left intentionally blank
		 */
	}

}
