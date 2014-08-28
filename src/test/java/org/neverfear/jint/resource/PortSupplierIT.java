package org.neverfear.jint.resource;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.mockito.internal.matchers.GreaterOrEqual;
import org.neverfear.jint.resource.CoordinatedSharedFileBinder;
import org.neverfear.jint.resource.PortSupplier;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PortSupplierIT {

	private static final int UPPER = 1204;
	private static final int LOWER = 1200;

	private static final int PORT = 1234;

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	private PortSupplier subject;

	private List<Closeable> sockets;

	@Before
	public void before() {
		this.sockets = newArrayList();
		this.subject = new PortSupplier(LOWER,
				UPPER);
	}

	@After
	public void after() throws Exception {
		for (final Closeable closeable : this.sockets) {
			closeable.close();
		}

		for (final File file : CoordinatedSharedFileBinder.SHARED_PORT_DIRECTORY.listFiles()) {
			Files.delete(file.toPath());
		}
	}

	private ServerSocket bindTo(final int port) throws Exception {
		final ServerSocket socket = new ServerSocket();
		try {
			socket.bind(new InetSocketAddress(port));
		} catch (final IOException e) {
			socket.close();
			throw e;
		}
		this.sockets.add(socket);
		return socket;
	}

	@Test
	public void whenRandomPort_expectPositiveNonZeroPort() throws IOException {
		final int port = PortSupplier.randomPort();
		assertThat(port, new GreaterOrEqual<>(0));
	}

	@Test
	public void givenRandomPort_whenAvailablePort_expectTrue() throws IOException {
		final int port = PortSupplier.randomPort();
		assertThat(port, new GreaterOrEqual<>(0));
		assertTrue(PortSupplier.isAvailable(port));
	}

	@Test
	public void givenValidPort_whenCheckPort_expectNothing() {
		PortSupplier.validatePort(PortSupplier.LOWEST_PORT);
		PortSupplier.validatePort(PortSupplier.HIGHEST_PORT);

		// Success is measured by reaching this far
	}

	@Test
	public void givenNegativePort_whenCheckPort_expectIllegalArgumentException() {
		/*
		 * Then
		 */
		this.expectedException.expect(IllegalArgumentException.class);

		/*
		 * When
		 */
		PortSupplier.validatePort(-1);
	}

	@Test
	public void givenLargePort_whenCheckPort_expectIllegalArgumentException() {
		/*
		 * Then
		 */
		this.expectedException.expect(IllegalArgumentException.class);

		/*
		 * When
		 */
		PortSupplier.validatePort(65536);
	}

	@Test
	public void givenValidPort_whenIsValidPort_expectTrue() {
		assertTrue(PortSupplier.isValidPort(PortSupplier.LOWEST_PORT));
		assertTrue(PortSupplier.isValidPort(PortSupplier.HIGHEST_PORT));
	}

	@Test
	public void givenInvalidPorts_whenIsValidPort_expectFalse() {
		assertFalse(PortSupplier.isValidPort(-1));
		assertFalse(PortSupplier.isValidPort(65536));
	}

	/**
	 * Note if some process decides to bind to port {@link #PORT} then this test
	 * will fail
	 */
	@Test
	public void givenPortAvailable_whenIsAvailable_expectTrue() throws Exception {
		/*
		 * When
		 */
		final boolean actual = PortSupplier.isAvailable(PORT);

		/*
		 * Then
		 */
		assertTrue(actual);
	}

	@Test
	public void givenPortUnavailable_whenIsAvailable_expectFalse() throws Exception {
		/*
		 * Given
		 */
		bindTo(PORT);

		/*
		 * When
		 */
		final boolean actual = PortSupplier.isAvailable(PORT);

		/*
		 * Then
		 */
		assertFalse(actual);
	}

	@Test
	public void givenFirstPortAvailable_whenNext_expect1200() throws Exception {
		/*
		 * When
		 */
		final int port = this.subject.next();

		/*
		 * Then
		 */
		assertEquals(LOWER, port);
	}

	@Test
	public void givenAllAvailable_whenNextFourTimes_expect1200Then1201Then1202Then1203() throws Exception {
		/*
		 * When
		 */
		final int port1 = this.subject.next();
		bindTo(port1);

		final int port2 = this.subject.next();
		bindTo(port2);

		final int port3 = this.subject.next();
		bindTo(port3);

		final int port4 = this.subject.next();

		/*
		 * Then
		 */
		assertEquals(LOWER, port1);
		assertEquals(LOWER + 1, port2);
		assertEquals(LOWER + 2, port3);
		assertEquals(LOWER + 3, port4);
	}

	@Test
	public void givenFirstPortUnavailable_whenNext_expect1201() throws Exception {
		/*
		 * Given
		 */
		bindTo(LOWER);

		/*
		 * When
		 */
		final int port = this.subject.next();

		/*
		 * Then
		 */
		assertEquals(1201, port);
	}

	@Test
	public void givenAllUnavailable_whenNextFourTimes_expectNoSuchElementException() throws Exception {
		/*
		 * Given
		 */
		bindTo(LOWER);
		bindTo(LOWER + 1);
		bindTo(LOWER + 2);
		bindTo(LOWER + 3);

		/*
		 * Then
		 */
		this.expectedException.expect(NoSuchElementException.class);

		/*
		 * When
		 */
		this.subject.next();
	}

	@Test
	public void givenAllAvailable_whenNextTwice_expect1200Then1201() throws Exception {
		/*
		 * When
		 */
		final int port1 = this.subject.next();
		final int port2 = this.subject.next();

		/*
		 * Then
		 */
		assertEquals(1200, port1);
		assertEquals(1201, port2);
	}

	/**
	 * Different than
	 * {@link #givenAllUnavailable_whenNextFourTimes_expectNoSuchElementException()}
	 * by grabbing a port successfully, then on another call to next it will
	 * start from a different point and wrap
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenSecondPortClaimed_whenNextTwiceTimes_expect1200Then1202() throws Exception {
		/*
		 * Given
		 */
		bindTo(1201);

		/*
		 * When
		 */
		final int port1 = this.subject.next();
		final int port2 = this.subject.next();

		/*
		 * Then
		 */
		assertEquals(1200, port1);
		assertEquals(1202, port2);
	}

	@Test
	public void whenGivenLowerIsEqualToUpperBound_whenConstruct_expectIllegalArgumentException() {
		/*
		 * Given
		 */
		final int lower = 1000;
		final int upper = 1000;

		/*
		 * Then
		 */
		this.expectedException.expect(IllegalArgumentException.class);

		/*
		 * When
		 */
		new PortSupplier(lower,
				upper);
	}

	@Test
	public void whenGivenLowerIsGreaterThanUpperBound_whenConstruct_expectIllegalArgumentException() {
		/*
		 * Given
		 */
		final int lower = 1001;
		final int upper = 1000;

		/*
		 * Then
		 */
		this.expectedException.expect(IllegalArgumentException.class);

		/*
		 * When
		 */
		new PortSupplier(lower,
				upper);
	}
}
