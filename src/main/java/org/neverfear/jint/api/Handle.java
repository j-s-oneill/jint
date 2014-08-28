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
 * Implementors of this interface are handles to process instances. They may be
 * running in any location.
 * 
 * @author doug@neverfear.org
 * 
 */
public interface Handle
	extends HandleCommon {

	/**
	 * Blocks until the handle has exited.
	 * 
	 * @throws InterruptedException
	 * @throws ApplicationException
	 */
	void await() throws InterruptedException, ApplicationException;
}
