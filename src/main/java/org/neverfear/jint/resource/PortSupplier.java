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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.NoSuchElementException;

import com.google.common.base.Supplier;

/**
 * Class that when given a port range will dispense port after port until the
 * entire range is full.
 * 
 * Dispensed ports begin at the lower bound and wrap at the upper bound.
 * 
 * @author doug@neverfear.org
 * 
 */
public class PortSupplier
	implements Supplier<Integer> {

	public static final int HIGHEST_PORT = 0xFFFF;
	public static final int LOWEST_PORT = 0;
	public static final int LOWEST_UNPRIVILEGED_PORT = 1024;

	private final int lower;
	private final int upper;
	private final PortBinder binder;

	private final int maxAttempts;

	private int current;

	/**
	 * Default constructor that provides an implementation that uses the entire
	 * unprivileged range of ports.
	 */
	public PortSupplier() {
		this(LOWEST_UNPRIVILEGED_PORT,
				HIGHEST_PORT);
	}

	/**
	 * Determine if a port is available by attempting to bind a socket to it.
	 * Most of the time this is sufficient and is a good choice for applications
	 * that are given their ports via arguments or configuration.
	 * 
	 * @param lower The lower bound (inclusive)
	 * @param upper The upper bound (exclusive)
	 */
	public PortSupplier(final int lower, final int upper) {
		this(lower,
				upper,
				new CoordinatedSharedFileBinder());
	}

	/**
	 * Uses a custom binding implementation.
	 * 
	 * @param lower The lower bound (inclusive)
	 * @param upper The upper bound (exclusive)
	 * @param binder Custom binder that tries to bind. This may be used to avoid
	 *        a concurrent situation where the port you asked for is available
	 *        but then acquired before you could use it.
	 */
	public PortSupplier(final int lower, final int upper, final PortBinder binder) {
		if (lower >= upper) {
			throw new IllegalArgumentException("lower bound was >= upper bound");
		}
		validatePort(lower);
		validatePort(upper);

		this.lower = lower;
		this.upper = upper;
		this.current = lower;
		this.binder = binder;

		this.maxAttempts = (upper - lower);
	}

	/**
	 * 
	 * @param port port number to check
	 * @return true if valid, false otherwise
	 */
	public static boolean isValidPort(final int port) {
		return (port >= LOWEST_PORT && port <= HIGHEST_PORT);
	}

	/**
	 * 
	 * @param port the port number to validate
	 * @throws IllegalArgumentException if the port is invalid
	 */
	public static void validatePort(final int port) throws IllegalArgumentException {
		if (!isValidPort(port)) {
			throw new IllegalArgumentException("Port not within valid range "
					+ LOWEST_PORT + "-" + HIGHEST_PORT + "(inclusive): " + port);
		}
	}

	/**
	 * Tests whether a passed port is currently available.
	 * 
	 * @param port
	 * @return
	 */
	public static boolean isAvailable(final int port) {
		try (final ServerSocket socket = new ServerSocket()) {
			socket.setReuseAddress(true);
			socket.bind(new InetSocketAddress(port));
		} catch (final IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Locate the next available port
	 * 
	 * @return
	 * @throws NoSuchElementException No such ports within the entire range are
	 *         available.
	 */
	public int next() {
		for (int attempt = 0; attempt < this.maxAttempts; attempt++) {
			if (this.binder.tryBind(this.current++)) {
				return this.current - 1;
			}

			if (this.current >= this.upper) {
				this.current = this.lower;
			}
		}

		throw new NoSuchElementException("No ports between " + this.lower + " to " + this.upper + " are available");
	}

	/**
	 * Get the any available port
	 * 
	 * @return
	 * @throws IOException
	 */
	public static int randomPort() throws IOException {
		try (final ServerSocket socket = new ServerSocket()) {
			socket.setReuseAddress(true);
			socket.bind(new InetSocketAddress(0));
			return socket.getLocalPort();
		}
	}

	@Override
	public Integer get() {
		return Integer.valueOf(next());
	}

}
