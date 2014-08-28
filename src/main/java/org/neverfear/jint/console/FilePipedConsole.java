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

import static java.util.concurrent.Executors.newFixedThreadPool;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import org.neverfear.jint.api.Console;
import org.neverfear.jint.util.JintUtil;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * <p>
 * Given stdin, stdout and stderr this implementation pipes content from those
 * streams into files and then uses those files to support efficient piping to
 * users. This accomplishes two goals
 * </p>
 * 
 * <ul>
 * <li>Content from the real streams is not consumed by readers of separate
 * instances of the {@link OutputStream} provided by {@link #output()} and
 * {@link #error()}</li>
 * <li>Providing a means whereby the entire history of interaction with the
 * console is recorded in files that may be inspected by an observer</li>
 * </ul>
 * 
 * @author doug@neverfear.org
 */
public final class FilePipedConsole
	implements Console, Closeable {

	/**
	 * 
	 */
	private static final ThreadFactory DAEMON_THREAD_FACTORY = new ThreadFactoryBuilder()
			.setDaemon(true)
			.setNameFormat(FilePipedConsole.class.getSimpleName() + "-%d")
			.build();

	private final File inputFile;
	private final File outputFile;
	private final File errorFile;

	private final Object stdinLock = new Object();
	private final OutputStream realStdin;
	private OutputStream stdin = null;

	private final IoCopyToFileMultiplexerTask stdOutMultiplexer;
	private final IoCopyToFileMultiplexerTask stdErrMultiplexer;

	/**
	 * 
	 * @param stdin the real stdin
	 * @param stdout the real stdout
	 * @param stderr the real stderr
	 * @param executor
	 * @throws IOException
	 */
	FilePipedConsole(final OutputStream stdin,
			final InputStream stdout,
			final InputStream stderr,
			final ExecutorService executor)
			throws IOException {
		this.inputFile = createDestinationFile("stdin");
		this.outputFile = createDestinationFile("stdout");
		this.errorFile = createDestinationFile("stderr");

		/*
		 * These tasks will terminate by themselves once the streams are at EOF
		 * or are closed
		 */
		this.stdOutMultiplexer = new IoCopyToFileMultiplexerTask(stdout,
				this.outputFile);
		this.stdErrMultiplexer = new IoCopyToFileMultiplexerTask(stderr,
				this.errorFile);

		this.realStdin = stdin;
		executor.execute(this.stdOutMultiplexer);
		executor.execute(this.stdErrMultiplexer);
	}

	/**
	 * 
	 * @param stdin the real stdin
	 * @param stdout the real stdout
	 * @param stderr the real stderr
	 * @throws IOException
	 */
	public FilePipedConsole(final OutputStream stdin,
			final InputStream stdout,
			final InputStream stderr)
			throws IOException {
		this(stdin,
				stdout,
				stderr,
				newFixedThreadPool(2, DAEMON_THREAD_FACTORY));
	}

	/**
	 * Users are responsible for closing this stream.
	 */
	@Override
	public InputStream output() throws IOException {
		return this.stdOutMultiplexer.connect();
	}

	/**
	 * Users are responsible for closing this stream.
	 */
	@Override
	public InputStream error() throws IOException {
		return this.stdErrMultiplexer.connect();
	}

	/**
	 * Users should avoid closing this stream themselves and use
	 * {@link #close()} instead.
	 */
	@Override
	public OutputStream input() throws IOException {
		synchronized (this.stdinLock) {
			if (this.stdin == null) {
				this.stdin = new LoggedOutputStream(this.realStdin,
						this.inputFile);
			}
		}
		return this.stdin;
	}

	public File inputFile() {
		return this.inputFile;
	}

	public File outputFile() {
		return this.outputFile;
	}

	public File errorFile() {
		return this.errorFile;
	}

	/**
	 * Closes stdin only. stdout and stderr are kept open and the user of this
	 * class must close them once there's no more data to be read.
	 */
	@Override
	public void close() throws IOException {
		synchronized (this.stdinLock) {
			if (this.stdin != null) {
				this.stdin.close();
			} else {
				this.realStdin.close();
			}
		}
	}

	private static File createDestinationFile(final String streamName) throws IOException {
		final File destination = JintUtil.createTempFile(FilePipedConsole.class,
			streamName);
		destination.createNewFile();
		if (JintUtil.shouldDeleteConsoleFilesOnExit()) {
			destination.deleteOnExit();
		}
		return destination;
	}

	public static FilePipedConsole fromProcess(final Process process) throws IOException {
		return new FilePipedConsole(process.getOutputStream(),
				process.getInputStream(),
				process.getErrorStream());
	}

}
