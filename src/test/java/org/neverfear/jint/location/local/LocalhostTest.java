package org.neverfear.jint.location.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.neverfear.jint.api.Description;
import org.neverfear.jint.api.Handle;
import org.neverfear.jint.location.local.Localhost;

import com.google.common.collect.Lists;

public class LocalhostTest {

	private Description mockDescription;
	private Localhost subject;

	@Before
	public void before() throws Exception {
		this.mockDescription = mock(Description.class);
		{
			when(this.mockDescription.command()).thenReturn(Lists.newArrayList("java"));
			// when(this.mockDescription.workingDirectory()).thenReturn(PropertyUtil.workingDirectory());
			when(this.mockDescription.isIOInherited()).thenReturn(false);
			// when(this.mockDescription.isErrorMappedToOutput()).thenReturn(true);
			// final Map<String, String> environment = Maps.newHashMap();
			// when(this.mockDescription.environment()).thenReturn(environment);
		}
		this.subject = new Localhost();
	}

	@Test
	public void givenLocalhost_whenInvokeToString_expectHostname() {
		/*
		 * When
		 */
		final String actual = this.subject.toString();

		/*
		 * Then
		 */
		assertEquals(Localhost.HOSTNAME, actual);
	}

	@Test
	public void givenLocalhost_whenInvokeGetHostname_expectHostname() {
		/*
		 * When
		 */
		final String actual = this.subject.getHostname();

		/*
		 * Then
		 */
		assertEquals(Localhost.HOSTNAME, actual);
	}

	@Test
	public void givenLocalhost_whenInvokeCreate_expectHandleIsNotRunningAndLocationIsLocalhost() {
		/*
		 * When
		 */
		final Handle handle = this.subject.create(this.mockDescription);

		/*
		 * Then
		 */
		assertFalse(handle.isRunning());
		assertEquals(this.subject, handle.location());
	}
}
