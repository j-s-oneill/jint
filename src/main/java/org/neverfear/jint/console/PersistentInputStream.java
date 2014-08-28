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

import static java.lang.Thread.interrupted;
import static java.lang.Thread.yield;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This {@link InputStream} will read from a {@link InputStream} until close is
 * called, at this point the {@link InputStream} is considered at the
 * end-of-file.
 * 
 * This class initialises the
 * 
 * @author doug@neverfear.org
 * 
 */
public final class PersistentInputStream
	extends InputStream {

	/**
	 * No more data may be read.
	 */
	public static final int EOF = -1;

	private final InputStream input;

	/**
	 * EOF will be returned once the total read bytes is equal or greater than
	 * the limit
	 */
	private volatile long readLimit = Long.MAX_VALUE;
	private final AtomicLong totalRead = new AtomicLong();

	/**
	 * 
	 * @param input
	 */
	public PersistentInputStream(final InputStream input) {
		this.input = input;
	}

	/**
	 * Blocks until file is closed or the current thread is interrupted.
	 * 
	 * @see {@link InputStream}
	 * @return The byte value or {@link #EOF} if the file no longer exists and
	 *         we have reached the end of file.
	 * @throws InterruptedIOException If the thread is interrupted
	 */
	@Override
	public int read() throws IOException {
		final byte[] buf = new byte[1];
		if (read(buf, 0, 1) == EOF) {
			return EOF;
		}
		return buf[0];
	}

	private int doRead(final byte[] buffer, final int offset, final int length) throws IOException {
		return this.input.read(buffer, offset, length);
	}

	/**
	 * Blocks until file is closed or the current thread is interrupted.
	 * 
	 * @see {@link InputStream}
	 * @return The byte count read into the buffer or {@link #EOF} if the file
	 *         no longer exists and we have reached the end of file.
	 * @throws InterruptedIOException If the thread is interrupted
	 */
	@Override
	public int read(final byte[] b, final int off, final int len) throws IOException {
		int count = EOF;
		while ((count = doRead(b, off, len)) == EOF) {
			if (this.totalRead.get() >= this.readLimit) {
				return EOF;
			}

			if (interrupted()) {
				throw new InterruptedIOException();
			}

			yield();
		}
		this.totalRead.addAndGet(count);
		return count;
	}

	@Override
	public int available() throws IOException {
		return this.input.available();
	}

	/**
	 * Marks the end of file for the {@link InputStream}.
	 * 
	 * @param totalSize the total size after which reads will return EOF, a
	 *        value of 0 stops immediately.
	 */
	public void endOfFile(final long totalSize) {
		this.readLimit = totalSize;
	}

	@Override
	public void close() throws IOException {
		endOfFile(0);
		this.input.close();
	}
}
