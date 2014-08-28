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
package org.neverfear.jint.resource;

import java.io.File;
import java.io.IOException;

import org.neverfear.jint.util.JintUtil;

/**
 * Uses a shared directory on the system to coordinate ports allocated between
 * processes. This may still result in a race with uncoordinated processes.
 * 
 * @author doug@neverfear.org
 * 
 */
public final class CoordinatedSharedFileBinder
	implements PortBinder {

	public static final File SHARED_PORT_DIRECTORY = new File(JintUtil.tempDirectory(),
			"port");

	private final SharedInteger sharedPort = new SharedInteger(SHARED_PORT_DIRECTORY);

	@Override
	public boolean tryBind(final int port) {
		if (!PortSupplier.isAvailable(port)) {
			return false;
		}

		try {
			return this.sharedPort.tryAcquire(port);
		} catch (final IOException e) {
			throw new IllegalStateException("Error acquiring port " + port,
					e);
		}
	}

}