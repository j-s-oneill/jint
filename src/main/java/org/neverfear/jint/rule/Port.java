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
package org.neverfear.jint.rule;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.google.common.base.Supplier;

/**
 * The {@link Port} rule may be used to allocate uncontended ports.
 * 
 * NOTE: There is a bit of a race when using this, it may find a port that is
 * free initially but soon becomes in use.
 * 
 * @author doug@neverfear.org
 * 
 */
// TODO: THis should use the disk to coordinate with other concurrent instances
// about which ports are allocated
public class Port
	implements TestRule, Supplier<Integer> {

	public static final int LOWEST_VALID_PORT = 1;

	/**
	 * Lowest port that you can bind to. Note that technically you can bind to
	 * port 0 but on most platforms this results in any port being bound to.
	 */
	public static final int LOWEST_PORT = 1;

	/**
	 * Lowest port you can bind to without extra privileges.
	 */
	public static final int LOWEST_UNPRIVILEGED_PORT = 1024;

	/**
	 * Highest port that you can bind to.
	 */
	public static final int HIGHEST_PORT = 65535;

	private final Random random = ThreadLocalRandom.current();

	private final int lowest;
	private final int highest;
	private int current = Integer.MIN_VALUE;

	private Port(final int lowest, final int highest) {
		validatePort(lowest);
		validatePort(highest);
		if (lowest > highest) {
			throw new IllegalArgumentException("Lower port should be numerically less than the higher port");
		}

		this.lowest = lowest;
		this.highest = highest;
	}

	@Override
	public Integer get() {
		if (this.current < 0) {
			throw new IllegalStateException("No port set");
		}

		return Integer.valueOf(this.current);
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				assign();
				try {
					base.evaluate();
				} finally {
					unassign();
				}
			}
		};
	}

	private void assign() throws IOException {
		if (this.lowest == LOWEST_VALID_PORT || this.highest == HIGHEST_PORT) {
			this.current = randomPort();
		} else {
			final int maxAttempts = this.highest - this.lowest;
			// TODO: This needs testing for off by one, you can set the seed
			// which
			// will probably help
			final int initial = (this.random.nextInt(Integer.MAX_VALUE) % maxAttempts) + this.lowest;
			this.current = probeForAvailablePort(initial, this.lowest, this.highest);
		}
	}

	private void unassign() {
		this.current = Integer.MIN_VALUE;
	}

	private int probeForAvailablePort(final int initial, final int lower, final int upper) throws IOException {
		final int maxAttempts = upper - lower;
		int current = initial;
		for (int attempt = 0; attempt < maxAttempts; attempt++, current++) {
			if (isAvailable(current)) {
				return current;
			}

			if (current >= upper) {
				current = lower;
			}
		}

		throw new NoSuchElementException("No ports between "
				+ lower + " to " + upper + " (inclusive) are available");
	}

	/**
	 * 
	 * @param port the port number to validate
	 * @throws IllegalArgumentException if the port is invalid
	 */
	public static void validatePort(final int port) throws IllegalArgumentException {
		if (!isValidPort(port)) {
			throw new IllegalArgumentException("Port not within valid range "
					+ LOWEST_VALID_PORT + "-" + HIGHEST_PORT + "(inclusive): " + port);
		}
	}

	/**
	 * 
	 * @param port port number to check
	 * @return true if valid, false otherwise
	 */
	public static boolean isValidPort(final int port) {
		return (port >= LOWEST_VALID_PORT && port <= HIGHEST_PORT);
	}

	public static boolean isUnprivilegedPort(final int port) {
		return (port >= LOWEST_UNPRIVILEGED_PORT && port <= HIGHEST_PORT);
	}

	public static Port any() {
		return new Port(LOWEST_VALID_PORT,
				HIGHEST_PORT);
	}

	public static Port withinRange(final int lowest, final int highest) {
		return new Port(lowest,
				highest);
	}

	public static boolean isAvailable(final int port) throws IOException {
		try (final ServerSocket socket = new ServerSocket()) {
			socket.setReuseAddress(true);
			socket.bind(new InetSocketAddress(port));
			return true;
		} catch (final IOException e) {
			return false;
		}
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

}
