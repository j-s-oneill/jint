package org.neverfear.jint.console;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FilePipedConsoleTest {

	@Rule
	public TestRule timeout = new DisableOnDebug(Timeout.seconds(1));

	/**
	 * Something bigger than we actually need so we can detect too many tasks
	 * running
	 */
	private static final int TASK_COUNT = 20;

	private OutputStream mockStdIn;

	private PrintWriter stdoutWriter;
	private PrintWriter stderrWriter;

	private FilePipedConsole subject;

	private ThreadPoolExecutor realExecutor;
	private ExecutorService spiedExecutor;

	@Before
	public void before() throws Exception {
		this.mockStdIn = mock(OutputStream.class, "stdin");
		this.realExecutor = newFixedThreadPool(TASK_COUNT);
		this.spiedExecutor = spy(this.realExecutor);

		final PipedOutputStream stdoutFeeder = new PipedOutputStream();
		this.stdoutWriter = new PrintWriter(stdoutFeeder);
		final PipedInputStream stdOut = new PipedInputStream(stdoutFeeder);

		final PipedOutputStream stderrFeeder = new PipedOutputStream();
		this.stderrWriter = new PrintWriter(stderrFeeder);
		final PipedInputStream stdErr = new PipedInputStream(stderrFeeder);

		this.subject = new FilePipedConsole(
				this.mockStdIn,
				stdOut,
				stdErr,
				this.spiedExecutor);
	}

	public static ThreadPoolExecutor newFixedThreadPool(final int nThreads) {
		return new ThreadPoolExecutor(nThreads,
				nThreads,
				0L,
				TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
	}

	@After
	public void after() throws Exception {
		this.subject.close();
		this.realExecutor.shutdown();
	}

	@Test
	public void whenConstruct_expectLaunchTwoTasks() throws Exception {
		/*
		 * Then
		 */
		verify(this.spiedExecutor, times(2)).execute(any(Runnable.class));
		assertEquals(2, this.realExecutor.getActiveCount());
	}

	@Test
	public void whenInvokeInputTwice_expectSameOutputStream() throws Exception {
		/*
		 * Given
		 */
		final OutputStream expected = this.subject.input();

		/*
		 * When
		 */
		final OutputStream actual = this.subject.input();

		/*
		 * Then
		 */
		assertEquals(expected, actual);
	}

	/**
	 * stdout and stderr are not closed because they will naturally drain and
	 * close themselves once the process has exited or the backing streams have
	 * otherwise been closed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void whenInvokeClose_expectCloseStdInOnly() throws Exception {
		/*
		 * When
		 */
		this.subject.close();

		/*
		 * Then
		 */
		verify(this.mockStdIn, atLeastOnce()).close();
	}

	@Test
	public void givenContentOnStdOut_whenInvokeNewOutput_expectNewInputStreamLinkedToStartOfStdOut()
			throws Exception {
		/*
		 * Given
		 */
		final char expected = 'A';
		this.stdoutWriter.print(expected);
		this.stdoutWriter.flush();

		/*
		 * When
		 */
		final InputStream output = this.subject.output();

		/*
		 * Then
		 */
		final char actual = (char) output.read();
		assertEquals(expected, actual);
	}

	@Test
	public void givenContentOnStdOut_whenInvokeNewOutputTwice_expectDifferentInstances_andBothReadSameData()
			throws Exception {
		/*
		 * Given
		 */
		final char expected = 'A';
		this.stdoutWriter.print(expected);
		this.stdoutWriter.flush();

		/*
		 * When
		 */
		final InputStream output1 = this.subject.output();
		final InputStream output2 = this.subject.output();

		/*
		 * Then
		 */
		assertNotEquals(output1, output2);
		assertEquals(expected, (char) output1.read());
		assertEquals(expected, (char) output2.read());
	}

	@Test
	public void givenContentOnStdErr_whenInvokeNewError_expectNewInputStreamLinkedToStartOfStdErr()
			throws Exception {
		/*
		 * Given
		 */
		final char expected = 'B';
		this.stderrWriter.print(expected);
		this.stderrWriter.flush();

		/*
		 * When
		 */
		final InputStream output = this.subject.error();

		/*
		 * Then
		 */
		final char actual = (char) output.read();
		assertEquals(expected, actual);
	}

	@Test
	public void givenContentOnStdErr_whenInvokeNewErrorTwice_expectDifferentInstances_andBothReadSameData()
			throws Exception {
		/*
		 * Given
		 */
		final char expected = 'B';
		this.stderrWriter.print(expected);
		this.stderrWriter.flush();

		/*
		 * When
		 */
		final InputStream output1 = this.subject.error();
		final InputStream output2 = this.subject.error();

		/*
		 * Then
		 */
		assertNotEquals(output1, output2);
		assertEquals(expected, (char) output1.read());
		assertEquals(expected, (char) output2.read());
	}

	@Test
	public void givenContentOnStdOut_whenClose_expectReadContent() throws Exception {
		/*
		 * Given
		 */
		this.stdoutWriter.println("ABC");
		this.stdoutWriter.flush();

		/*
		 * When
		 */
		this.subject.close();

		/*
		 * Then
		 */
		final BufferedReader reader = new BufferedReader(new InputStreamReader(this.subject.output()));
		assertEquals("ABC", reader.readLine());
	}

	@Test
	public void givenContentOnStdOut_andReaderOpen_whenClose_expectReadContent() throws Exception {
		/*
		 * Given
		 */
		this.stdoutWriter.println("ABC");
		this.stdoutWriter.flush();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(this.subject.output()));

		/*
		 * When
		 */
		this.subject.close();

		/*
		 * Then
		 */
		assertEquals("ABC", reader.readLine());
	}

	@Test
	public void givenContentOnStdErr_whenClose_expectReadContent() throws Exception {
		/*
		 * Given
		 */
		this.stderrWriter.println("ABC");
		this.stderrWriter.flush();

		/*
		 * When
		 */
		this.subject.close();

		/*
		 * Then
		 */
		final BufferedReader reader = new BufferedReader(new InputStreamReader(this.subject.error()));
		assertEquals("ABC", reader.readLine());
	}

	@Test
	public void givenContentOnStdErr_andReaderOpen_whenClose_expectReadContent() throws Exception {
		/*
		 * Given
		 */
		this.stderrWriter.println("ABC");
		this.stderrWriter.flush();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(this.subject.error()));

		/*
		 * When
		 */
		this.subject.close();

		/*
		 * Then
		 */
		assertEquals("ABC", reader.readLine());
	}

}
