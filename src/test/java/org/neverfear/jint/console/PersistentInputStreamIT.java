package org.neverfear.jint.console;

import static java.io.File.createTempFile;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.interrupted;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersistentInputStreamIT {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private static final String DATA = "ABC";
	private PrintWriter writeEndPoint;
	private PersistentInputStream subject;

	@Before
	public void before() throws Exception {
		final File file = createTempFile(PersistentInputStreamIT.class.getCanonicalName(), "TESTFILE");
		file.deleteOnExit();

		this.writeEndPoint = new PrintWriter(
				new OutputStreamWriter(
						new FileOutputStream(file)));

		this.subject = new PersistentInputStream(
				new FileInputStream(file));
	}

	@After
	public void after() {
		/*
		 * Unset the flag since some tests set it but subsequently ignore it
		 */
		interrupted();
	}

	/**
	 * This may change in the future.
	 */
	@Test
	public void whenMarkSupported_expectFalse() {
		assertFalse(this.subject.markSupported());
	}

	@Test
	public void givenNoData_whenAvailable_expectZero() throws Exception {
		/*
		 * When
		 */
		final int available = this.subject.available();

		/*
		 * Then
		 */
		assertEquals(0, available);
	}

	@Test
	public void givenClosed_whenReadAvailable_expectIOException() throws Exception {
		/*
		 * Given
		 */
		this.subject.close();

		/*
		 * Then
		 */
		this.expectedException.expect(IOException.class);
		this.expectedException.expectMessage("Stream Closed");

		/*
		 * When
		 */
		this.subject.available();
	}

	@Test
	public void givenClosed_whenReadNoArgs_expectIOException() throws Exception {
		/*
		 * Given
		 */
		this.subject.close();

		/*
		 * Then
		 */
		this.expectedException.expect(IOException.class);
		this.expectedException.expectMessage("Stream Closed");

		/*
		 * When
		 */
		this.subject.read();
	}

	@Test
	public void givenClosed_whenReadBuffer_expectIOException() throws Exception {
		/*
		 * Given
		 */
		this.subject.close();

		/*
		 * Then
		 */
		this.expectedException.expect(IOException.class);
		this.expectedException.expectMessage("Stream Closed");

		/*
		 * When
		 */
		this.subject.read(new byte[1024]);
	}

	@Test
	public void givenEOF_whenReadAvailable_expectZero() throws Exception {
		/*
		 * Given
		 */
		this.subject.endOfFile(0);

		/*
		 * When
		 */
		final int available = this.subject.available();

		/*
		 * Then
		 */
		assertEquals(0, available);
	}

	@Test
	public void givenEOF_whenReadNoArgs_expectEOF() throws Exception {
		/*
		 * Given
		 */
		this.subject.endOfFile(0);

		/*
		 * When
		 */
		final int count = this.subject.read();

		/*
		 * Then
		 */
		assertEquals(PersistentInputStream.EOF, count);
	}

	@Test
	public void givenEOF_whenReadBuffer_expectEOF() throws Exception {
		/*
		 * Given
		 */
		this.subject.endOfFile(0);

		/*
		 * When
		 */
		final int count = this.subject.read(new byte[1024]);

		/*
		 * Then
		 */
		assertEquals(PersistentInputStream.EOF, count);
	}

	/*
	 * Timeout is required because of the loop.
	 */
	@Test(timeout = 2500)
	public void givenDataAvailable_whenAvailable_expectDataLength() throws Exception {
		/*
		 * Given
		 */
		this.writeEndPoint.print(DATA);
		this.writeEndPoint.flush();

		/*
		 * When
		 */
		int available;
		while ((available = this.subject.available()) == 0) {
			// Wait until available is not 0 then exit
			if (interrupted()) {
				// Be a good citizen to the test runner
				throw new InterruptedException();
			}
		}

		/*
		 * Then
		 */
		assertEquals(DATA.getBytes().length, available);
	}

	@Test
	public void givenDataAvailable_whenReadNoArgs_expectFirstByte() throws Exception {
		/*
		 * Given
		 */
		this.writeEndPoint.print(DATA);
		this.writeEndPoint.flush();

		/*
		 * When
		 */
		final int first = this.subject.read();
		final int second = this.subject.read();
		final int third = this.subject.read();

		/*
		 * Then
		 */
		assertEquals(DATA.charAt(0), first);
		assertEquals(DATA.charAt(1), second);
		assertEquals(DATA.charAt(2), third);
	}

	@Test(expected = InterruptedIOException.class)
	public void givenInterrupted_andNoData_whenReadNoArgs_expectInterruptedIOException() throws Exception {
		/*
		 * Given
		 */
		currentThread().interrupt();

		/*
		 * When
		 */
		this.subject.read();
	}

	public void givenInterrupted_andDataAvailable_whenReadNoArgs_expectReadIsSuccessful() throws Exception {
		/*
		 * Given
		 */
		this.writeEndPoint.println(DATA);
		this.writeEndPoint.flush();

		currentThread().interrupt();

		/*
		 * When
		 */
		final int value = this.subject.read();

		/*
		 * Then
		 */
		assertEquals(DATA.charAt(0), value);
	}

	@Test
	public void givenDataAvailable_whenReadBuffer_expectBufferFilledAndReadCountIsCorrect() throws Exception {
		/*
		 * Given
		 */
		final byte[] buffer = new byte[1024];
		this.writeEndPoint.print(DATA);
		this.writeEndPoint.flush();

		/*
		 * When
		 */
		final int count = this.subject.read(buffer);

		/*
		 * Then
		 */
		{
			// ABC then \n
			assertEquals(DATA.getBytes().length, count);
			final byte[] trimmedBuffer = Arrays.copyOfRange(buffer, 0, count);
			assertArrayEquals(DATA.getBytes(), trimmedBuffer);
		}
	}

	@Test(expected = InterruptedIOException.class)
	public void givenInterrupted_andNoDataAvailable_whenReadBuffer_expectInterruptedIOException() throws Exception {
		/*
		 * Given
		 */
		final byte[] buffer = new byte[1024];
		currentThread().interrupt();

		/*
		 * When
		 */
		this.subject.read(buffer);
	}

	@Test
	public void givenInterrupted_andDataAvailable_whenReadBuffer_expectBufferFilledAndReadCountCorrect()
			throws Exception {
		/*
		 * Given
		 */
		final byte[] buffer = new byte[1024];
		this.writeEndPoint.print(DATA);
		this.writeEndPoint.flush();
		currentThread().interrupt();

		/*
		 * When
		 */
		final int count = this.subject.read(buffer);
		{
			// ABC then \n
			assertEquals(DATA.getBytes().length, count);
			final byte[] trimmedBuffer = Arrays.copyOfRange(buffer, 0, count);
			assertArrayEquals(DATA.getBytes(), trimmedBuffer);
		}
	}

}
