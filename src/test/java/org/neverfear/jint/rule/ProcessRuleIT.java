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
package org.neverfear.jint.rule;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author doug@neverfear.org
 * 
 */
public class ProcessRuleIT {

	private static final String SAMPLE_VARIABLE = "SAMPLE_VARIABLE";
	private static final String SAMPLE_FILE = "sample_file";

	@ClassRule
	public static TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Rule
	public TestRule timeout = new DisableOnDebug(Timeout.seconds(1));

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Rule
	public ProcessRule ls = ProcessRule.create("/bin/ls", "-l")
			.workingDirectory(temporaryFolder.getRoot()
					.getPath());

	@Rule
	public ProcessRule cat = ProcessRule.exec("/bin/cat");

	@Rule
	public ProcessRule env = ProcessRule.create("/usr/bin/env")
			.env(SAMPLE_VARIABLE, 10L);

	@BeforeClass
	public static void beforeClass() throws IOException {
		temporaryFolder.newFile(SAMPLE_FILE)
				.createNewFile();
	}

	/**
	 * Shows that the {@link ProcessRule#workingDirectory(String)} works
	 * correctly.
	 */
	@Test
	public void givenLs_andSampleFile_whenReadOutput_expectSampleFile() throws Exception {
		boolean found = false;
		final Process process = this.ls.get();
		final InputStream inputStream = process.getInputStream();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.contains(SAMPLE_FILE)) {
					found = true;
				}
			}
		}
		assertTrue(found);
	}

	/**
	 * Shows that {@link ProcessRule#env(String, Object)} works correctly.
	 */
	@Test
	public void givenEnv_andSampleVariable_whenReadOutput_expectSampleVariableIsTen() throws Exception {
		String found = null;
		final Process process = this.env.get();
		final InputStream inputStream = process.getInputStream();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith(SAMPLE_VARIABLE)) {
					found = line;
				}
			}
		}
		assertEquals(SAMPLE_VARIABLE + "=10", found);
	}

	/**
	 * Shows that {@link ProcessRule#exec(String)} works correctly.
	 */
	@Test
	public void givenCat_whenWriteLine_expectReadBackSameLine() throws Exception {
		final Process process = this.cat.get();
		final InputStream inputStream = process.getInputStream();
		final OutputStream outputStream = process.getOutputStream();
		try (BufferedReader stdout = new BufferedReader(new InputStreamReader(inputStream));
				PrintWriter stdin = new PrintWriter(outputStream)) {
			stdin.println("I expect cat to echo this back to me");
			stdin.flush();

			assertEquals("I expect cat to echo this back to me", stdout.readLine());
		}
	}

	/**
	 * Shows that the user is free to destroy the process.
	 */
	@Test
	public void invokeStop_expectShouldMakeNoDifference() {
		final Process process = this.ls.get();
		process.destroy();
	}

	/**
	 * Shows that the user may wait for the exit code
	 */
	@Test
	public void givenLs_whenWaitForExitCode_expectZero() throws Exception {
		final Process process = this.ls.get();
		assertEquals(0, process.waitFor());
	}

	@Test
	public void givenEmptyCommandLine_whenCreate_expectIllegalArgumentException() {
		this.expectedException.expect(IllegalArgumentException.class);
		this.expectedException.expectMessage("supply a command");
		ProcessRule.create();
	}

	@Test
	public void givenEmptyCommandLine_whenExec_expectIllegalArgumentException() {
		this.expectedException.expect(IllegalArgumentException.class);
		this.expectedException.expectMessage("supply a command");
		ProcessRule.exec("");
	}

	@Test
	public void givenNullKey_whenEnv_expectIllegalArgumentException() {
		this.expectedException.expect(IllegalArgumentException.class);
		this.expectedException.expectMessage("null key");

		final String key = null;
		final Object value = 10L;

		ProcessRule.exec("java")
				.env(key, value);
	}

	@Test
	public void givenNullValue_whenEnv_expectIllegalArgumentException() {
		this.expectedException.expect(IllegalArgumentException.class);
		this.expectedException.expectMessage("null value");

		final String key = SAMPLE_VARIABLE;
		final Object value = null;

		ProcessRule.exec("java")
				.env(key, value);
	}

	@Test
	public void givenUnknownDirectory_whenWorkingDirectory_expectIllegalArgumentException() {
		this.expectedException.expect(IllegalArgumentException.class);
		this.expectedException.expectMessage("does not exist");

		ProcessRule.exec("java")
				.workingDirectory("i/do.not/exist");
	}

	@Test
	public void givenAppliedRule_andAppendToInMemoryBuffer_whenEvaluate_expectEachPhaseOfLifeCycleLogged()
			throws Throwable {
		/*
		 * Given
		 */
		final ProcessRule rule = ProcessRule.create("ls", "directory with space");
		final Statement statement = rule.apply(mock(Statement.class),
			mock(Description.class));

		try (final BufferedReader reader = new BufferedReader(new FileReader("logs/ProcessRule.log"))) {

			/*
			 * When
			 */
			statement.evaluate();

			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.contains("Starting: ls \"directory with space\"")) {
					break;
				}
			}

			/*
			 * Then
			 */
			assertThat(line, containsString("Starting: ls \"directory with space\""));
			assertThat(reader.readLine(), containsString("Started: ls \"directory with space\""));
			assertThat(reader.readLine(), containsString("Stopping: ls \"directory with space\""));
			assertThat(reader.readLine(), containsString("Stopped: ls \"directory with space\""));
		}
	}
}
