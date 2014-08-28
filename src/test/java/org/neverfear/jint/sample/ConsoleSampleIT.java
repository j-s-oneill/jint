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
package org.neverfear.jint.sample;

import static org.junit.Assert.assertEquals;
import static org.neverfear.jint.api.Jint.basic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.neverfear.jint.api.Console;
import org.neverfear.jint.application.basic.BasicApplication;

/**
 * Demonstrates console interactions.
 * 
 * @author doug@neverfear.org
 * 
 */
public class ConsoleSampleIT {

	@Rule
	public TestRule timeout = new DisableOnDebug(Timeout.seconds(5));

	private final BasicApplication happyCat = basic("cat")
			.build();

	private final BasicApplication sadCat = basic("cat")
			.arguments("bad argument")
			.build();

	@Before
	public void before() throws Exception {
		this.sadCat.start();
		this.sadCat.awaitStart();

		this.happyCat.start();
		this.happyCat.awaitStart();
	}

	@After
	public void after() throws Exception {
		this.happyCat.stop();
		this.happyCat.awaitStop();

		this.sadCat.stop();
		this.sadCat.awaitStop();
	}

	@Test
	public void givenSadCat_whenReadFromStdErr_expectBadArgumentError() throws Exception {
		final Console console = this.sadCat.console();
		final BufferedReader stderr = new BufferedReader(new InputStreamReader(console.error()));
		assertEquals("cat: bad argument: No such file or directory", stderr.readLine());
	}

	@Test
	public void givenHappyCat_whenWrite_expectReadSameData() throws Exception {
		final Console console = this.happyCat.console();
		final PrintWriter stdin = new PrintWriter(console.input(),
				true);
		final BufferedReader stdout = new BufferedReader(new InputStreamReader(console.output()));

		stdin.println("ABC");
		assertEquals("ABC", stdout.readLine());
	}

	@Test
	public void givenHappyCat_whenCloseConsole_expectExitCodeZero() throws Exception {
		this.happyCat.console()
				.close();
		this.happyCat.awaitStop();
		assertEquals(0, this.happyCat.exitCode());
	}

	@Test
	public void givenHappyCatWithDataOnStdOut_whenStop_expectReadSameData() throws Exception {
		/*
		 * Given
		 */
		final Console console = this.happyCat.console();
		final PrintWriter stdin = new PrintWriter(console.input(),
				true);
		stdin.println("ABC");
		stdin.close();

		/*
		 * When
		 */
		this.happyCat.stop();

		/*
		 * Then
		 */
		final BufferedReader stdout = new BufferedReader(new InputStreamReader(console.output()));
		assertEquals("ABC", stdout.readLine());
	}
}
