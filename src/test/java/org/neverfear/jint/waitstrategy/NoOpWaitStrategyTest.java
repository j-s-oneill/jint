package org.neverfear.jint.waitstrategy;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Before;
import org.junit.Test;
import org.neverfear.jint.api.Application;
import org.neverfear.jint.waitstrategy.NoOpWaitStrategy;

public class NoOpWaitStrategyTest {

	private NoOpWaitStrategy subject;

	@Before
	public void before() {
		this.subject = NoOpWaitStrategy.INSTANCE;
	}

	@Test
	public void givenStrategy_whenWaitFor_expectDoesNotInteractWithApplication() {
		/*
		 * When
		 */
		final Application application = mock(Application.class);
		this.subject.waitFor(application);

		/*
		 * Then
		 */
		verifyZeroInteractions(application);
	}
}
