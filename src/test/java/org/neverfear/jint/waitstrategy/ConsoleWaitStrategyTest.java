package org.neverfear.jint.waitstrategy;

import static java.lang.Thread.currentThread;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatcher;
import org.neverfear.jint.api.Application;
import org.neverfear.jint.api.ApplicationException;
import org.neverfear.jint.api.Console;

public class ConsoleWaitStrategyTest {

	private static final String UNRELATED_LINE_1 = "Ignore me";
	private static final String SEARCH_FOR_LINE = "Where is wally?";
	private static final String UNRELATED_LINE_2 = "I'm never read";
	private static final Pattern PATTERN = Pattern.compile(".*wally.*");

	private ConsoleWaitStrategy subject;
	private Application mockApplication;
	private Console mockConsole;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void before() throws Exception {
		this.mockConsole = mock(Console.class);
		this.mockApplication = mock(Application.class);
		when(this.mockApplication.console()).thenReturn(this.mockConsole);
		when(this.mockApplication.isRunning()).thenReturn(true);

		this.subject = new ConsoleWaitStrategy(PATTERN);
	}

	@Test
	public void givenBrokenInputStream_whenInvokeWaitFor_expectApplicationException() throws Exception {
		/*
		 * Given
		 */
		final IOException exception = new IOException();
		when(this.mockConsole.output()).thenThrow(exception);

		/*
		 * Then
		 */
		this.expectedException.expect(ApplicationException.class);
		this.expectedException.expectCause(new ArgumentMatcher<Throwable>() {

			@Override
			public boolean matches(final Object argument) {
				return exception.equals(argument);
			}

		});

		/*
		 * When
		 */
		this.subject.waitFor(this.mockApplication);
	}

	@Test
	public void givenMatcherDoesNotMatch_whenInvokeWaitFor_expectApplicationException() throws Exception {
		/*
		 * Given
		 */
		final byte[] data = createData(
			UNRELATED_LINE_1,
			UNRELATED_LINE_2);
		when(this.mockConsole.output()).thenReturn(new ByteArrayInputStream(data));

		/*
		 * Then
		 */
		this.expectedException.expect(ApplicationException.class);

		/*
		 * When
		 */
		this.subject.waitFor(this.mockApplication);
	}

	@Test
	public void givenCurrentThreadIsInterrupted_whenInvokeWaitFor_expectInterruptedException()
			throws Exception {
		/*
		 * Given
		 */
		final byte[] data = createData(
				UNRELATED_LINE_1);
		when(this.mockConsole.output()).thenReturn(new ByteArrayInputStream(data));
		currentThread().interrupt();

		/*
		 * Then
		 */
		this.expectedException.expect(InterruptedException.class);

		/*
		 * When
		 */
		this.subject.waitFor(this.mockApplication);
	}

	@Test
	public void givenMatcherMatchesFirstLine_whenInvokeWaitFor_expectSuccess() throws Exception {
		/*
		 * Given
		 */
		final byte[] data = createData(
			SEARCH_FOR_LINE,
			UNRELATED_LINE_2);
		when(this.mockConsole.output()).thenReturn(new ByteArrayInputStream(data));

		/*
		 * When
		 */
		this.subject.waitFor(this.mockApplication);
	}

	@Test
	public void givenMatcherMatchesSecondLine_whenInvokeWaitFor_expectSuccess() throws Exception {
		/*
		 * Given
		 */
		final byte[] data = createData(UNRELATED_LINE_1,
			SEARCH_FOR_LINE,
			UNRELATED_LINE_2);
		when(this.mockConsole.output()).thenReturn(new ByteArrayInputStream(data));

		/*
		 * When
		 */
		this.subject.waitFor(this.mockApplication);
	}

	@Test
	public void givenMatcherMatchesLastLine_whenInvokeWaitFor_expectSuccess() throws Exception {
		/*
		 * Given
		 */
		final byte[] data = createData(UNRELATED_LINE_1,
			SEARCH_FOR_LINE);
		when(this.mockConsole.output()).thenReturn(new ByteArrayInputStream(data));

		/*
		 * When
		 */
		this.subject.waitFor(this.mockApplication);
	}

	private static byte[] createData(final String... lines) {
		final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		final PrintWriter writer = new PrintWriter(new OutputStreamWriter(buffer));
		for (final String line : lines) {
			writer.println(line);
		}
		writer.close();
		return buffer.toByteArray();
	}

}
