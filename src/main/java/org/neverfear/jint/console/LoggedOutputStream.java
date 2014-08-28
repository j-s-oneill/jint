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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.neverfear.jint.util.ThrowableBuilder;

/**
 * An {@link OutputStream} that writes to the provided {@link OutputStream} but
 * then also tries the same operation on a logging {@link OutputStream}.
 * 
 * @author doug@neverfear.org
 * 
 */
public class LoggedOutputStream
	extends OutputStream {

	private final Object lock = new Object();
	private final ThrowableBuilder<IOException> throwableBuilder = ThrowableBuilder.create();

	private final OutputStream out;
	private final OutputStream log;

	public LoggedOutputStream(final OutputStream out, final OutputStream log) {
		this.out = out;
		this.log = log;
	}

	public LoggedOutputStream(final OutputStream out, final File logFile)
			throws FileNotFoundException {
		this(out,
				new FileOutputStream(logFile));
	}

	@Override
	public void write(final int b) throws IOException {
		synchronized (this.lock) {
			try {
				this.out.write(b);
			} catch (final IOException e) {
				this.throwableBuilder.add(e);
			}

			try {
				this.log.write(b);
			} catch (final IOException e) {
				this.throwableBuilder.add(e);
			}

			this.throwableBuilder.throwIfSet();
		}
	}

	@Override
	public void write(final byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {
		synchronized (this.lock) {
			try {
				this.out.write(b, off, len);
			} catch (final IOException e) {
				this.throwableBuilder.add(e);
			}

			try {
				this.log.write(b, off, len);
			} catch (final IOException e) {
				this.throwableBuilder.add(e);
			}

			this.throwableBuilder.throwIfSet();
		}
	}

	@Override
	public void flush() throws IOException {
		synchronized (this.lock) {
			try {
				this.out.flush();
			} catch (final IOException e) {
				this.throwableBuilder.add(e);
			}

			try {
				this.log.flush();
			} catch (final IOException e) {
				this.throwableBuilder.add(e);
			}

			this.throwableBuilder.throwIfSet();
		}
	}

	@Override
	public void close() throws IOException {
		synchronized (this.lock) {
			try {
				this.out.close();
			} catch (final IOException e) {
				this.throwableBuilder.add(e);
			}

			try {
				this.log.close();
			} catch (final IOException e) {
				this.throwableBuilder.add(e);
			}

			this.throwableBuilder.throwIfSet();
		}
	}

}
