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
package org.neverfear.jint.console;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author doug@neverfear.org
 * 
 */
public final class IoCopyToFileMultiplexerTask
	implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(IoCopyToFileMultiplexerTask.class);

	private static final int EOF = -1;
	private static final int BUFFER_SIZE = 0x1000;

	private final Collection<PersistentInputStream> connected = new LinkedList<>();

	private final InputStream input;
	private final OutputStream output;
	private final File outputFile;

	private long totalSize = 0;
	private boolean endOfFileReached = false;

	public IoCopyToFileMultiplexerTask(final InputStream input, final File outputFile)
			throws FileNotFoundException {
		this.input = input;
		this.outputFile = outputFile;
		this.output = new FileOutputStream(outputFile);
	}

	public InputStream connect() throws IOException {
		synchronized (this.connected) {
			final PersistentInputStream stream = new PersistentInputStream(new FileInputStream(this.outputFile));
			if (this.endOfFileReached) {
				stream.endOfFile(this.totalSize);
			} else {
				this.connected.add(stream);
			}
			return stream;
		}
	}

	private void endOfFile() {
		synchronized (this.connected) {
			for (final PersistentInputStream stream : this.connected) {
				stream.endOfFile(this.totalSize);
			}
			this.endOfFileReached = true;
			this.connected.clear();
		}
	}

	@Override
	public void run() {

		final byte[] buffer = new byte[BUFFER_SIZE];

		// Close `to` at the end of loop
		try (OutputStream to = this.output;
				InputStream from = new BufferedInputStream(this.input)) {
			int readCount;
			while ((readCount = from.read(buffer)) != EOF) {
				to.write(buffer, 0, readCount);
				to.flush();
				this.totalSize += readCount;
			}
		} catch (final IOException e) {
			/*
			 * Can't really do anything useful with this except log it at the
			 * lowest possible level.
			 */
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Task to copy to " + this.outputFile, e);
			}
		} finally {
			endOfFile();
		}
	}

}
