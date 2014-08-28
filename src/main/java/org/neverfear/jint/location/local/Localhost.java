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
package org.neverfear.jint.location.local;

import org.neverfear.jint.api.Description;
import org.neverfear.jint.api.Handle;
import org.neverfear.jint.api.Location;

/**
 * Represents the current local host. This location does not consume any
 * additional resources so you may create one as many times as you like.
 * 
 * @author doug@neverfear.org
 * 
 */
public final class Localhost
	implements Location {

	public static final String HOSTNAME = "localhost";

	@Override
	public Handle create(final Description description) {
		return new LocalHandle(this,
				description);
	}

	@Override
	public String toString() {
		return getHostname();
	}

	@Override
	public String getHostname() {
		return HOSTNAME;
	}

}
