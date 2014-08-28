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

/**
 * Represents a location that the {@link Handle} of an application may run upon.
 * 
 * @author doug@neverfear.org
 * 
 */
public interface Location {

	/**
	 * Creates an instance of an application {@link Handle} at this location
	 * using the provided description.
	 * 
	 * @param description
	 * @return
	 */
	Handle create(final Description description);

	/**
	 * The host name of the location. This does not mean the host name is
	 * network reachable.
	 * 
	 * @return the host name of this location.
	 */
	/*
	 * Currently I cannot fathom a location that does not have a host name. If
	 * this world view changes then this may become "name" and provisions put in
	 * place to distinguish between locations that are on a network and those
	 * that are not.
	 */
	String getHostname();
}
