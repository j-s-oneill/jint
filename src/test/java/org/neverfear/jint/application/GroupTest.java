package org.neverfear.jint.application;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.neverfear.jint.matcher.JintMatchers.causedBy;
import static org.neverfear.jint.matcher.JintMatchers.rootCause;
import static org.neverfear.jint.matcher.JintMatchers.surpressed;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.mockito.internal.matchers.And;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.neverfear.jint.api.Application;
import org.neverfear.jint.api.ApplicationException;

import com.google.common.collect.Lists;

/**
 * The application set up in this test looks like this.
 * 
 * <pre>
 *         +---+
 *   +---->| B +-----+
 *   |     +---+     v
 * +-+-+           +---+
 * | A +---------->| C |
 * +---+           +---+
 * </pre>
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GroupTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private Group subject;

	private Application applicationA1;
	private Application applicationB1;
	private Application applicationC1;

	private Application applicationA2;
	private Application applicationB2;
	private Application applicationC2;

	private List<Application> startOrder;
	private List<Application> stopOrder;

	@Before
	public void before() throws Exception {
		this.startOrder = Lists.newArrayList();
		this.stopOrder = Lists.newArrayList();

		this.applicationA1 = mockApplication("A1", this.startOrder, this.stopOrder);
		this.applicationB1 = mockApplication("B1", this.startOrder, this.stopOrder);
		this.applicationC1 = mockApplication("C1", this.startOrder, this.stopOrder);

		this.applicationA2 = mockApplication("A2", this.startOrder, this.stopOrder);
		this.applicationB2 = mockApplication("B2", this.startOrder, this.stopOrder);
		this.applicationC2 = mockApplication("C2", this.startOrder, this.stopOrder);

		this.subject = Group.of(this.applicationC1, this.applicationC2)
				.then(this.applicationB1, this.applicationB2)
				.then(this.applicationA1, this.applicationA2);
	}

	@Test
	public void givenApplicationGroup_whenStartGroup_expectAllStartedAndAwaitStarted() throws Exception {
		/*
		 * When
		 */
		this.subject.start();

		/*
		 * Then
		 */
		verify(this.applicationC1).start();
		verify(this.applicationC1).awaitStart();

		verify(this.applicationC2).start();
		verify(this.applicationC2).awaitStart();

		verify(this.applicationB1).start();
		verify(this.applicationB1).awaitStart();

		verify(this.applicationB2).start();
		verify(this.applicationB2).awaitStart();

		verify(this.applicationA1).start();
		verify(this.applicationA1).awaitStart();

		verify(this.applicationA2).start();
		verify(this.applicationA2).awaitStart();
	}

	@Test
	public void givenApplicationGroup_whenStopGroup_expectAllStoppedAndAwaitStopped() throws Exception {
		/*
		 * When
		 */
		this.subject.stop();

		/*
		 * Then
		 */
		verify(this.applicationA1).stop();
		verify(this.applicationA1).awaitStop();

		verify(this.applicationA2).stop();
		verify(this.applicationA2).awaitStop();

		verify(this.applicationB1).stop();
		verify(this.applicationB1).awaitStop();

		verify(this.applicationB2).stop();
		verify(this.applicationB2).awaitStop();

		verify(this.applicationC1).stop();
		verify(this.applicationC1).awaitStop();

		verify(this.applicationC2).stop();
		verify(this.applicationC2).awaitStop();
	}

	@Test
	public void givenApplicationGroup_whenStartGroup_expectCorrectOrder() throws Exception {
		/*
		 * When
		 */
		this.subject.start();

		/*
		 * Then
		 */
		{
			assertTrue(this.startOrder.contains(this.applicationA1));
			assertTrue(this.startOrder.contains(this.applicationB1));
			assertTrue(this.startOrder.contains(this.applicationC1));

			assertTrue(this.startOrder.contains(this.applicationA2));
			assertTrue(this.startOrder.contains(this.applicationB2));
			assertTrue(this.startOrder.contains(this.applicationC2));

			final int indexA1 = this.startOrder.indexOf(this.applicationA1);
			final int indexB1 = this.startOrder.indexOf(this.applicationB1);
			final int indexC1 = this.startOrder.indexOf(this.applicationC1);

			final int indexA2 = this.startOrder.indexOf(this.applicationA2);
			final int indexB2 = this.startOrder.indexOf(this.applicationB2);
			final int indexC2 = this.startOrder.indexOf(this.applicationC2);

			assertTrue("C1:" + indexC1 + " B1:" + indexB1, indexC1 < indexB1);
			assertTrue("C2:" + indexC2 + " B1:" + indexB1, indexC2 < indexB1);

			assertTrue("C1:" + indexC1 + " B2:" + indexB2, indexC1 < indexB2);
			assertTrue("C2:" + indexC2 + " B2:" + indexB2, indexC2 < indexB2);

			assertTrue("B1:" + indexB1 + " A1:" + indexA1, indexB1 < indexA1);
			assertTrue("B2:" + indexB2 + " A1:" + indexA1, indexB2 < indexA1);

			assertTrue("B1:" + indexB1 + " A2:" + indexA2, indexB1 < indexA2);
			assertTrue("B2:" + indexB2 + " A2:" + indexA2, indexB2 < indexA2);
		}
	}

	@Test
	public void givenApplicationGroup_whenStopGroup_expectReverseOrder() throws Exception {
		/*
		 * When
		 */
		this.subject.stop();

		/*
		 * Then
		 */
		{
			assertTrue(this.stopOrder.contains(this.applicationA1));
			assertTrue(this.stopOrder.contains(this.applicationB1));
			assertTrue(this.stopOrder.contains(this.applicationC1));

			assertTrue(this.stopOrder.contains(this.applicationA2));
			assertTrue(this.stopOrder.contains(this.applicationB2));
			assertTrue(this.stopOrder.contains(this.applicationC2));

			final int indexA1 = this.stopOrder.indexOf(this.applicationA1);
			final int indexB1 = this.stopOrder.indexOf(this.applicationB1);
			final int indexC1 = this.stopOrder.indexOf(this.applicationC1);

			final int indexA2 = this.stopOrder.indexOf(this.applicationA2);
			final int indexB2 = this.stopOrder.indexOf(this.applicationB2);
			final int indexC2 = this.stopOrder.indexOf(this.applicationC2);

			assertTrue("A1:" + indexA1 + " B1:" + indexB1, indexA1 < indexB1);
			assertTrue("A2:" + indexA2 + " B1:" + indexB1, indexA2 < indexB1);

			assertTrue("A1:" + indexA1 + " B2:" + indexB2, indexA1 < indexB2);
			assertTrue("A2:" + indexA2 + " B2:" + indexB2, indexA2 < indexB2);

			assertTrue("B1:" + indexB1 + " C1:" + indexC1, indexB1 < indexC1);
			assertTrue("B2:" + indexB2 + " C1:" + indexC1, indexB2 < indexC1);

			assertTrue("B1:" + indexB1 + " C2:" + indexC2, indexB1 < indexC2);
			assertTrue("B2:" + indexB2 + " C2:" + indexC2, indexB2 < indexC2);
		}
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void givenFirstApplicationThrowsExceptionOnStart_whenStartGroup_expectApplicationExceptionWhichIsCausedByOriginalException()
			throws Exception {
		/*
		 * Given
		 */
		final RuntimeException exceptionC = new RuntimeException("C");
		doThrow(exceptionC)
				.when(this.applicationC1)
				.start();

		/*
		 * Then
		 */
		final Matcher instanceOf = instanceOf(ApplicationException.class);
		final Matcher causedBy = causedBy(exceptionC);
		final List<Matcher> matchers = asList(instanceOf, causedBy);
		this.expectedException.expect(new And(matchers));

		/*
		 * When
		 */
		this.subject.start();
	}

	@Test
	public void givenFirstApplicationThrowsExceptionOnStart_whenStartGroup_expectNoInteractionWithOtherApplications()
			throws Exception {
		/*
		 * Given
		 */
		doThrow(ApplicationException.class)
				.when(this.applicationC1)
				.start();

		/*
		 * When
		 */
		boolean exceptionCaught = false;
		try {
			this.subject.start();
		} catch (final ApplicationException e) {
			exceptionCaught = true;
		}

		/*
		 * Then
		 */
		{
			assertTrue(exceptionCaught);

			{
				// C1 is in an unknown state, so do not stop it
				verify(this.applicationC1).start();
				verify(this.applicationC1, never()).awaitStart();
				verify(this.applicationC1, never()).stop();
				verify(this.applicationC1, never()).awaitStop();

				if (this.startOrder.contains(this.applicationC2)) {
					verify(this.applicationC2, never()).awaitStart();
					verify(this.applicationC2, never()).stop();
					verify(this.applicationC2, never()).awaitStop();
				}
			}

			{
				verify(this.applicationB1, never()).start();
				verify(this.applicationB2, never()).start();

				verify(this.applicationB1, never()).awaitStart();
				verify(this.applicationB2, never()).awaitStart();

				verify(this.applicationB1, never()).stop();
				verify(this.applicationB2, never()).stop();

				verify(this.applicationB1, never()).awaitStop();
				verify(this.applicationB2, never()).awaitStop();
			}

			{
				verify(this.applicationA1, never()).start();
				verify(this.applicationA2, never()).start();

				verify(this.applicationA1, never()).awaitStart();
				verify(this.applicationA2, never()).awaitStart();

				verify(this.applicationA1, never()).stop();
				verify(this.applicationA2, never()).stop();

				verify(this.applicationA1, never()).awaitStop();
				verify(this.applicationA2, never()).awaitStop();
			}
		}
	}

	@Test
	public void givenSecondApplicationThrowsExceptionOnStart_whenStartGroup_expectFirstApplicationStoppedAndNoFurtherInteractions()
			throws Exception {
		/*
		 * Given
		 */
		doThrow(ApplicationException.class)
				.when(this.applicationB1)
				.start();

		/*
		 * When
		 */
		boolean exceptionCaught = false;
		try {
			this.subject.start();
		} catch (final ApplicationException e) {
			exceptionCaught = true;
		}

		/*
		 * Then
		 */
		{
			assertTrue(exceptionCaught);

			{
				verify(this.applicationC1).start();
				verify(this.applicationC2).start();

				verify(this.applicationC1).awaitStart();
				verify(this.applicationC2).awaitStart();

				verify(this.applicationC1).stop();
				verify(this.applicationC2).stop();

				verify(this.applicationC1).awaitStop();
				verify(this.applicationC2).awaitStop();
			}

			{
				// B1 is in an unknown state, so do not stop it
				verify(this.applicationB1).start();
				verify(this.applicationB1, never()).awaitStart();
				verify(this.applicationB1, never()).stop();
				verify(this.applicationB1, never()).awaitStop();

				if (this.startOrder.contains(this.applicationB2)) {
					verify(this.applicationB2, never()).awaitStart();
					verify(this.applicationB2, never()).stop();
					verify(this.applicationB2, never()).awaitStop();
				}
			}

			{
				verify(this.applicationA1, never()).start();
				verify(this.applicationA2, never()).start();

				verify(this.applicationA1, never()).awaitStart();
				verify(this.applicationA2, never()).awaitStart();

				verify(this.applicationA1, never()).stop();
				verify(this.applicationA2, never()).stop();

				verify(this.applicationA1, never()).awaitStop();
				verify(this.applicationA2, never()).awaitStop();
			}
		}
	}

	@Test
	public void givenLastApplicationThrowsExceptionOnStart_whenStartGroup_expectFirstAndSecondApplicationStoppedButLastIsNot()
			throws Exception {
		/*
		 * Given
		 */
		final RuntimeException exceptionA = new RuntimeException("A");
		doThrow(exceptionA)
				.when(this.applicationA1)
				.start();

		/*
		 * When
		 */
		boolean exceptionCaught = false;
		try {
			this.subject.start();
		} catch (final ApplicationException e) {
			exceptionCaught = true;
		}

		/*
		 * Then
		 */
		{
			assertTrue(exceptionCaught);

			{
				verify(this.applicationC1).start();
				verify(this.applicationC2).start();

				verify(this.applicationC1).awaitStart();
				verify(this.applicationC2).awaitStart();

				verify(this.applicationC1).stop();
				verify(this.applicationC2).stop();

				verify(this.applicationC1).awaitStop();
				verify(this.applicationC2).awaitStop();
			}

			{
				verify(this.applicationB1).start();
				verify(this.applicationB2).start();

				verify(this.applicationB1).awaitStart();
				verify(this.applicationB2).awaitStart();

				verify(this.applicationB1).stop();
				verify(this.applicationB2).stop();

				verify(this.applicationB1).awaitStop();
				verify(this.applicationB2).awaitStop();
			}

			{
				// A1 is in an unknown state, so do not stop it
				verify(this.applicationA1).start();
				verify(this.applicationA1, never()).awaitStart();
				verify(this.applicationA1, never()).stop();
				verify(this.applicationA1, never()).awaitStop();

				if (this.startOrder.contains(this.applicationA2)) {
					verify(this.applicationA2, never()).awaitStart();
					verify(this.applicationA2, never()).stop();
					verify(this.applicationA2, never()).awaitStop();
				}
			}
		}
	}

	@Test
	public void givenFirstApplicationThrowsExceptionOnAwaitStart_whenStartGroup_expectFirstApplicationIsStartedThenStoppedAndNoOtherApplicationsAreInteracted()
			throws Exception {
		/*
		 * Given
		 */
		doThrow(ApplicationException.class)
				.when(this.applicationC1)
				.awaitStart();

		/*
		 * When
		 */
		boolean exceptionCaught = false;
		try {
			this.subject.start();
		} catch (final ApplicationException e) {
			exceptionCaught = true;
		}

		/*
		 * Then
		 */
		{
			assertTrue(exceptionCaught);

			{
				verify(this.applicationC1).start();
				verify(this.applicationC1).awaitStart();
				verify(this.applicationC1).stop();
				verify(this.applicationC1).awaitStop();

				if (this.startOrder.contains(this.applicationC2)) {
					verify(this.applicationC2, never()).awaitStart();
					verify(this.applicationC2).stop();
					verify(this.applicationC2).awaitStop();
				}
			}

			{
				verify(this.applicationB1, never()).start();
				verify(this.applicationB2, never()).start();

				verify(this.applicationB1, never()).awaitStart();
				verify(this.applicationB2, never()).awaitStart();

				verify(this.applicationB1, never()).stop();
				verify(this.applicationB2, never()).stop();

				verify(this.applicationB1, never()).awaitStop();
				verify(this.applicationB2, never()).awaitStop();
			}

			{
				verify(this.applicationA1, never()).start();
				verify(this.applicationA2, never()).start();

				verify(this.applicationA1, never()).awaitStart();
				verify(this.applicationA2, never()).awaitStart();

				verify(this.applicationA1, never()).stop();
				verify(this.applicationA2, never()).stop();

				verify(this.applicationA1, never()).awaitStop();
				verify(this.applicationA2, never()).awaitStop();
			}
		}
	}

	@Test
	public void givenSecondApplicationThrowsExceptionOnAwaitStart_whenStartGroup_expectFirstAndSecondApplicationIsStartedThenStoppedAndNoOtherApplicationsAreInteracted()
			throws Exception {
		/*
		 * Given
		 */
		doThrow(ApplicationException.class)
				.when(this.applicationB1)
				.awaitStart();

		/*
		 * When
		 */
		boolean exceptionCaught = false;
		try {
			this.subject.start();
		} catch (final ApplicationException e) {
			exceptionCaught = true;
		}

		/*
		 * Then
		 */
		{
			assertTrue(exceptionCaught);

			{
				verify(this.applicationC1).start();
				verify(this.applicationC2).start();

				verify(this.applicationC1).awaitStart();
				verify(this.applicationC2).awaitStart();

				verify(this.applicationC1).stop();
				verify(this.applicationC2).stop();

				verify(this.applicationC1).awaitStop();
				verify(this.applicationC2).awaitStop();
			}

			{
				verify(this.applicationB1).start();
				verify(this.applicationB1).awaitStart();
				verify(this.applicationB1).stop();
				verify(this.applicationB1).awaitStop();

				if (this.startOrder.contains(this.applicationB2)) {
					verify(this.applicationB2, never()).awaitStart();
					verify(this.applicationB2).stop();
					verify(this.applicationB2).awaitStop();
				}
			}

			{
				verify(this.applicationA1, never()).start();
				verify(this.applicationA2, never()).start();

				verify(this.applicationA1, never()).awaitStart();
				verify(this.applicationA2, never()).awaitStart();

				verify(this.applicationA1, never()).stop();
				verify(this.applicationA2, never()).stop();

				verify(this.applicationA1, never()).awaitStop();
				verify(this.applicationA2, never()).awaitStop();
			}
		}
	}

	@Test
	public void givenLastApplicationThrowsExceptionOnAwaitStart_whenStartGroup_expectEveryApplicationIsStartedThenStopped()
			throws Exception {
		/*
		 * Given
		 */
		doThrow(ApplicationException.class)
				.when(this.applicationA1)
				.awaitStart();

		/*
		 * When
		 */
		boolean exceptionCaught = false;
		try {
			this.subject.start();
		} catch (final ApplicationException e) {
			exceptionCaught = true;
		}

		/*
		 * Then
		 */
		{
			assertTrue(exceptionCaught);

			{
				verify(this.applicationC1).start();
				verify(this.applicationC2).start();

				verify(this.applicationC1).awaitStart();
				verify(this.applicationC2).awaitStart();

				verify(this.applicationC1).stop();
				verify(this.applicationC2).stop();

				verify(this.applicationC1).awaitStop();
				verify(this.applicationC2).awaitStop();
			}

			{
				verify(this.applicationB1).start();
				verify(this.applicationB2).start();

				verify(this.applicationB1).awaitStart();
				verify(this.applicationB2).awaitStart();

				verify(this.applicationB1).stop();
				verify(this.applicationB2).stop();

				verify(this.applicationB1).awaitStop();
				verify(this.applicationB2).awaitStop();
			}

			{
				verify(this.applicationA1).start();
				verify(this.applicationA1).awaitStart();
				verify(this.applicationA1).stop();
				verify(this.applicationA1).awaitStop();

				if (this.startOrder.contains(this.applicationA2)) {
					verify(this.applicationA2, never()).awaitStart();
					verify(this.applicationA2).stop();
					verify(this.applicationA2).awaitStop();
				}
			}
		}
	}

	/**
	 * This is an attempt to clean up on failure. The failed application will
	 * remain in an unknown state but the others will be cleaned.
	 */
	@Test
	public void givenFirstApplicationThrowsExceptionOnStop_whenStopAll_expectEveryApplicationIsStopped()
			throws Exception {
		/*
		 * Given
		 */
		doThrow(ApplicationException.class)
				.when(this.applicationC1)
				.stop();

		/*
		 * When
		 */
		boolean exceptionCaught = false;
		try {
			this.subject.stop();
		} catch (final ApplicationException e) {
			exceptionCaught = true;
		}

		/*
		 * Then
		 */
		{
			assertTrue(exceptionCaught);

			verifyStopAllApplications();
			verifyAwaitStopAllApplications();
		}
	}

	@Test
	public void givenSecondApplicationThrowsExceptionOnStop_whenStopAll_expectStopIsCalledOnAllOtherApplications()
			throws Exception {
		/*
		 * Given
		 */
		doThrow(ApplicationException.class)
				.when(this.applicationB1)
				.stop();

		/*
		 * When
		 */
		boolean exceptionCaught = false;
		try {
			this.subject.stop();
		} catch (final ApplicationException e) {
			exceptionCaught = true;
		}

		/*
		 * Then
		 */
		{
			assertTrue(exceptionCaught);

			verifyStopAllApplications();
			verifyAwaitStopAllApplications();
		}
	}

	@Test
	public void givenLastApplicationThrowsExceptionOnStop_whenStopAll_expectStopIsCalledOnAllOtherApplications()
			throws Exception {
		/*
		 * Given
		 */
		doThrow(ApplicationException.class)
				.when(this.applicationA1)
				.stop();

		/*
		 * When
		 */
		boolean exceptionCaught = false;
		try {
			this.subject.stop();
		} catch (final ApplicationException e) {
			exceptionCaught = true;
		}

		/*
		 * Then
		 */
		{
			assertTrue(exceptionCaught);

			verifyStopAllApplications();
			verifyAwaitStopAllApplications();
		}
	}

	@Test
	public void givenFirstApplicationThrowsExceptionOnAwaitStop_whenStopAll_expectEveryApplicationIsStopped()
			throws Exception {
		/*
		 * Given
		 */
		doThrow(ApplicationException.class)
				.when(this.applicationC1)
				.awaitStop();

		/*
		 * When
		 */
		boolean exceptionCaught = false;
		try {
			this.subject.stop();
		} catch (final ApplicationException e) {
			exceptionCaught = true;
		}

		/*
		 * Then
		 */
		{
			assertTrue(exceptionCaught);

			verifyStopAllApplications();

			/*
			 * Does not verify that each application is waited for, only that
			 * they are all stopped.
			 */
		}
	}

	@Test
	public void givenSecondApplicationThrowsExceptionOnAwaitStop_whenStopAll_expectEveryApplicationIsStopped()
			throws Exception {
		/*
		 * Given
		 */
		doThrow(ApplicationException.class)
				.when(this.applicationB1)
				.awaitStop();

		/*
		 * When
		 */
		boolean exceptionCaught = false;
		try {
			this.subject.stop();
		} catch (final ApplicationException e) {
			exceptionCaught = true;
		}

		/*
		 * Then
		 */
		{
			assertTrue(exceptionCaught);

			verifyStopAllApplications();

			/*
			 * Does not verify that each application is waited for, only that
			 * they are all stopped.
			 */
		}
	}

	@Test
	public void givenLastApplicationThrowsExceptionOnAwaitStop_whenStopAll_expectEveryApplicationIsStopped()
			throws Exception {
		/*
		 * Given
		 */
		doThrow(ApplicationException.class)
				.when(this.applicationA1)
				.awaitStop();

		/*
		 * When
		 */
		boolean exceptionCaught = false;
		try {
			this.subject.stop();
		} catch (final ApplicationException e) {
			exceptionCaught = true;
		}

		/*
		 * Then
		 */
		{
			assertTrue(exceptionCaught);

			verifyStopAllApplications();

			/*
			 * Does not verify that each application is waited for, only that
			 * they are all stopped.
			 */
		}
	}

	/**
	 * On a catastrophic failure, the exception thrown must contain all the
	 * information required to diagnose it. This means that every exception
	 * thrown on {@link Application#stop()} must be available in the resultant
	 * exception. The root cause should be the first exception that was thrown.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void givenEveryApplicationThrowsExceptionOnStop_whenStopAll_expectThrowsApplicationExceptionCausedByFirstExceptionAndSurpressedOtherException()
			throws Exception {
		/*
		 * Given
		 */
		final ApplicationException exceptionA = new ApplicationException("A");
		final ApplicationException exceptionB = new ApplicationException("B");
		final ApplicationException exceptionC = new ApplicationException("C");
		doThrow(exceptionA)
				.when(this.applicationA1)
				.stop();
		doThrow(exceptionB)
				.when(this.applicationB1)
				.stop();
		doThrow(exceptionC)
				.when(this.applicationC1)
				.stop();

		/*
		 * Then
		 */

		this.expectedException.expect(rootCause(equalTo((Object)
				exceptionA)));
		this.expectedException.expect(allOf(surpressed(hasItems(
			rootCause(equalTo((Object) exceptionB)),
			rootCause(equalTo((Object) exceptionC))))));

		/*
		 * When
		 */
		this.subject.stop();
	}

	/*
	 * Utilities
	 */

	private void verifyStopAllApplications() throws ApplicationException, InterruptedException {
		verify(this.applicationC1).stop();
		verify(this.applicationC2).stop();
		verify(this.applicationB1).stop();
		verify(this.applicationB2).stop();
		verify(this.applicationA1).stop();
		verify(this.applicationA2).stop();
	}

	private void verifyAwaitStopAllApplications() throws ApplicationException, InterruptedException {
		verify(this.applicationC1).awaitStop();
		verify(this.applicationC2).awaitStop();
		verify(this.applicationB1).awaitStop();
		verify(this.applicationB2).awaitStop();
		verify(this.applicationA1).awaitStop();
		verify(this.applicationA2).awaitStop();
	}

	private static Application mockApplication(final String name, final List<Application> startOrder,
			final List<Application> stopOrder) throws Exception {
		final Application application = mock(Application.class, name);
		final AtomicBoolean running = new AtomicBoolean();
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(final InvocationOnMock invocation) throws Throwable {
				startOrder.add(application);
				running.set(true);
				return null;
			}

		}).when(application)
				.start();

		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(final InvocationOnMock invocation) throws Throwable {
				stopOrder.add(application);
				running.set(false);
				return null;
			}

		}).when(application)
				.stop();

		doAnswer(new Answer<Boolean>() {

			@Override
			public Boolean answer(final InvocationOnMock invocation) throws Throwable {
				return running.get();
			}

		}).when(application)
				.isRunning();

		return application;
	}

}
