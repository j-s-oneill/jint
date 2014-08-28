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
package org.neverfear.jint.application.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.neverfear.jint.api.Jint;

import applications.ExitMain;
import applications.SleepMain;

/**
 * @author doug@neverfear.org
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JavaApplicationIT {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private JavaApplication exitApplication;
	private JavaApplication sleepApplication;

	@Before
	public void before() {
		this.exitApplication = Jint.java(ExitMain.class)
				.build();
		this.sleepApplication = Jint.java(SleepMain.class)
				.build();
	}

	@After
	public void after() throws Exception {
		this.exitApplication.stop();
		this.sleepApplication.stop();
	}

	@Test
	public void givenStarted_whenInvokeIsStarted_expectTrue() throws Exception {
		/*
		 * Given
		 */
		this.sleepApplication.start();

		/*
		 * When
		 */
		final boolean running = this.sleepApplication.isStarted();

		/*
		 * Then
		 */
		assertTrue(running);
	}

	@Test
	public void givenUnstarted_whenInvokeIsStarted_expectFalse() throws Exception {
		/*
		 * When
		 */
		final boolean running = this.sleepApplication.isStarted();

		/*
		 * Then
		 */
		assertFalse(running);
	}

	@Test
	public void givenStarted_thenStopped_whenInvokeIsStarted_expectFalse() throws Exception {
		/*
		 * Given
		 */
		this.sleepApplication.start();
		this.sleepApplication.stop();

		/*
		 * When
		 */
		final boolean running = this.sleepApplication.isStarted();

		/*
		 * Then
		 */
		assertFalse(running);
	}

	@Test
	public void givenStarted_whenAwaitStart_thenInvokeIsRunning_expectTrue() throws Exception {
		/*
		 * Given
		 */
		this.sleepApplication.start();
		this.sleepApplication.awaitStart();

		/*
		 * When
		 */
		final boolean running = this.sleepApplication.isRunning();

		/*
		 * Then
		 */
		assertTrue(running);
	}

	@Test
	public void givenUnstarted_whenInvokeIsRunning_expectFalse() throws Exception {
		/*
		 * When
		 */
		final boolean running = this.sleepApplication.isRunning();

		/*
		 * Then
		 */
		assertFalse(running);
	}

	@Test
	public void givenStarted_thenStopped_whenAwaitStop_thenInvokeIsRunning_expectFalse() throws Exception {
		/*
		 * Given
		 */
		this.sleepApplication.start();
		this.sleepApplication.stop();
		this.sleepApplication.awaitStop();

		/*
		 * When
		 */
		final boolean running = this.sleepApplication.isRunning();

		/*
		 * Then
		 */
		assertFalse(running);
	}

	@Test
	public void givenUnstarted_whenExitCode_expectIllegalStateException() throws Exception {
		/*
		 * Then
		 */
		this.expectedException.expect(IllegalStateException.class);
		this.expectedException.expectMessage("Never started");

		/*
		 * When
		 */
		this.exitApplication.exitCode();
	}

	@Test
	public void givenStarted_whenExitCode_expectIllegalStateException() throws Exception {
		/*
		 * Given
		 */
		this.exitApplication.start();

		/*
		 * Then
		 */
		this.expectedException.expect(IllegalStateException.class);
		this.expectedException.expectMessage("hasn't exited");

		/*
		 * When
		 */
		this.exitApplication.exitCode();
	}

	@Test
	public void givenStarted_thenAwaitStop_whenExitCode_expect12() throws Exception {
		/*
		 * Given
		 */
		this.exitApplication.start();
		this.exitApplication.awaitStop();

		/*
		 * When
		 */
		final int exitCode = this.exitApplication.exitCode();

		/*
		 * Then
		 */
		assertEquals(ExitMain.EXIT_CODE, exitCode);
	}

}
