package org.neverfear.jint.application.basic;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neverfear.jint.api.ApplicationException;
import org.neverfear.jint.api.Console;
import org.neverfear.jint.api.Handle;
import org.neverfear.jint.api.Location;
import org.neverfear.jint.api.WaitStrategy;

import com.google.common.collect.Maps;
import com.google.common.testing.EqualsTester;

public class BasicApplicationIT {

	private static final String EXECUTABLE = "skynet";
	private static final List<String> ARGUMENTS = newArrayList("A", "B", "C");
	private static final File FILE = new File("Some file path");

	private static final Object TOTALLY_DIFFERENT_TYPE = "Hello World";

	private BasicApplication application;
	private BasicApplication logicallyEqual;
	private BasicApplication logicallyUnequalBecauseOfHandle;
	private BasicApplication logicallyUnequalBecauseOfDescription;
	private BasicApplication logicallyUnequalBecauseOfWaitStrategy;

	private final BasicDescription differentDescription = mock(BasicDescription.class, withSettings()
			.name("Different description")
			.defaultAnswer(RETURNS_MOCKS));

	private Location mockLocation;
	private Handle mockHandle;
	private BasicDescription mockDescription;
	private WaitStrategy mockWaitStrategy;

	private Console mockConsole;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void before() throws Exception {

		this.mockConsole = mock(Console.class, "Mock console");

		this.mockLocation = mock(Location.class, "Mock location");
		{
			this.mockHandle = mock(Handle.class);
			when(this.mockHandle.location()).thenReturn(this.mockLocation);
			when(this.mockHandle.console()).thenReturn(this.mockConsole);
		}

		{
			final Map<String, String> environment = Maps.newHashMap();
			environment.put("A", "B");
			environment.put("C", "D");

			this.mockDescription = mock(BasicDescription.class, "Mock description");
			when(this.mockDescription.executable()).thenReturn(EXECUTABLE);
			when(this.mockDescription.arguments()).thenReturn(ARGUMENTS);
			when(this.mockDescription.command()).thenReturn(asList(EXECUTABLE, "A", "B", "C"));
			when(this.mockDescription.isErrorMappedToOutput()).thenReturn(true);
			when(this.mockDescription.workingDirectory()).thenReturn(FILE);
			when(this.mockDescription.isIOInherited()).thenReturn(false);
			when(this.mockDescription.environment()).thenReturn(environment);
		}

		this.mockWaitStrategy = mock(WaitStrategy.class);

		this.application = new BasicApplication(
				this.mockDescription,
				this.mockHandle,
				this.mockWaitStrategy);

		/*
		 * The following are used exclusively in equality tests
		 */
		{
			this.logicallyEqual = new BasicApplication(
					this.mockDescription,
					this.mockHandle,
					this.mockWaitStrategy);
			this.logicallyUnequalBecauseOfHandle = new BasicApplication(
					this.mockDescription,
					mock(Handle.class),
					this.mockWaitStrategy);
			this.logicallyUnequalBecauseOfDescription = new BasicApplication(
					this.differentDescription,
					this.mockHandle,
					this.mockWaitStrategy);
			this.logicallyUnequalBecauseOfWaitStrategy = new BasicApplication(
					this.mockDescription,
					this.mockHandle,
					mock(WaitStrategy.class));
		}
	}

	/*
	 * arguments() tests
	 */

	@Test
	public void givenBasicApplication_whenInvokeDescription_expectEqualsConstructedValue() throws Exception {
		/*
		 * When
		 */
		final BasicDescription actual = this.application.description();

		/*
		 * Then
		 */
		assertEquals(new ImmutableBasicDescription(this.mockDescription), actual);
	}

	@Test
	public void givenBasicApplication_whenInvokeToString_expectExpandedCommandInQuotes() throws Exception {
		final String expected = "\"" + EXECUTABLE + "\" \"A\" \"B\" \"C\"";

		/*
		 * When
		 */
		final String actual = this.application.toString();

		/*
		 * Then
		 */
		assertEquals(expected, actual);
	}

	/*
	 * location() tests
	 */

	@Test
	public void givenBasicApplication_whenInvokeLocation_expectEqualsConstructedValue() throws Exception {
		/*
		 * When
		 */
		final Location value = this.application.location();

		/*
		 * Then
		 */
		assertEquals(this.mockLocation, value);
	}

	/*
	 * start() tests
	 */

	@Test
	public void givenBasicApplication_whenInvokeStart_expectDelegateToHandle() throws Exception {
		/*
		 * When
		 */
		this.application.start();

		/*
		 * Then
		 */
		verify(this.mockHandle).start();
	}

	/*
	 * stop() tests
	 */

	@Test
	public void givenBasicApplication_whenInvokeStop_expectDelegateToHandle() throws Exception {
		/*
		 * When
		 */
		this.application.stop();

		/*
		 * Then
		 */
		verify(this.mockHandle).stop();
	}

	/*
	 * awaitStart() tests
	 */

	@Test
	public void givenBasicApplication_whenInvokeAwaitStart_expectDelegateToWaitStrategy_andNotToHandle()
			throws Exception {
		/*
		 * When
		 */
		this.application.awaitStart();

		/*
		 * Then
		 */
		verify(this.mockWaitStrategy).waitFor(this.application);
	}

	/*
	 * awaitStop() tests
	 */

	@Test
	public void givenHandleIsNotRunning_whenInvokeAwaitStop_expectDoesNotDelegateToHandle() throws Exception {
		/*
		 * Given
		 */
		when(this.mockHandle.isRunning()).thenReturn(false);

		/*
		 * When
		 */
		this.application.awaitStop();

		/*
		 * Then
		 */
		verify(this.mockHandle, never()).await();
	}

	@Test
	public void givenHandleIsRunning_whenInvokeAwaitStop_expectInvokeWaitForOnHandle() throws Exception {
		/*
		 * Given
		 */
		when(this.mockHandle.isRunning()).thenReturn(true);

		/*
		 * When
		 */
		this.application.awaitStop();

		/*
		 * Then
		 */
		verify(this.mockHandle).await();
	}

	/*
	 * standardError() tests
	 */

	@Test
	public void givenBasicApplicationAndMockConsole_whenInvokeGetConsole_expectDelegateToHandle()
			throws Exception {
		/*
		 * When
		 */
		assertEquals(this.mockConsole, this.application.console());

		/*
		 * Then
		 */
		verify(this.mockHandle).console();
	}

	/*
	 * exitCode() tests
	 */

	@Test
	public void givenHandleWithExitCode_whenInvokeExitCode_expectDelegateToHandle() throws Exception {
		/*
		 * Given
		 */
		when(this.mockHandle.exitCode()).thenReturn(123);

		/*
		 * When
		 */
		final int exitCode = this.application.exitCode();

		/*
		 * Then
		 */
		assertEquals(123, exitCode);
		verify(this.mockHandle).exitCode();
	}

	@Test
	public void givenHandleThrowsApplicationException_whenInvokeExitCode_expectRethrows()
			throws Exception {
		/*
		 * Given
		 */
		doThrow(ApplicationException.class)
				.when(this.mockHandle)
				.exitCode();

		/*
		 * Then
		 */
		this.expectedException.expect(ApplicationException.class);

		/*
		 * When
		 */
		this.application.exitCode();

		fail("Should not get this far");
	}

	/*
	 * isRunning() tests
	 */

	@Test
	public void givenHandleIsRunning_whenInvokeIsRunning_expectDelegateToHandle() throws Exception {
		/*
		 * Given
		 */
		when(this.mockHandle.isRunning()).thenReturn(true);

		/*
		 * When
		 */
		final boolean actual = this.application.isRunning();

		/*
		 * Then
		 */
		assertTrue(actual);
		verify(this.mockHandle).isRunning();
	}

	@Test
	public void givenApplications_whenInvokeEqualsAndHashCode_expectIdenticalInstancesAndLogicallyEqualApplicationsAreEqual_andEverythingElseIsNot()
			throws Exception {

		new EqualsTester().addEqualityGroup(this.application, this.logicallyEqual)
				.addEqualityGroup(this.logicallyUnequalBecauseOfHandle)
				.addEqualityGroup(this.logicallyUnequalBecauseOfDescription)
				.addEqualityGroup(this.logicallyUnequalBecauseOfWaitStrategy)
				.addEqualityGroup(TOTALLY_DIFFERENT_TYPE)
				.testEquals();
	}
}
