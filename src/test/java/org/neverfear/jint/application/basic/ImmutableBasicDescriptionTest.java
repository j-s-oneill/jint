package org.neverfear.jint.application.basic;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neverfear.jint.application.basic.BasicDescription;
import org.neverfear.jint.application.basic.ImmutableBasicDescription;

import com.google.common.collect.Maps;

public class ImmutableBasicDescriptionTest {

	private static final String EXECUTABLE = "skynet";
	private static final List<String> ARGUMENTS = newArrayList("A", "B", "C");
	private static final List<String> COMMAND = newArrayList(EXECUTABLE, "A", "B", "C");

	private static final File FILE = new File("Some file path");

	private BasicDescription mockDescription;
	private ImmutableBasicDescription description;

	private List<String> command;
	private List<String> arguments;
	private Map<String, String> environment;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void before() throws Exception {
		this.environment = Maps.newHashMap();
		this.environment.put("A", "B");
		this.environment.put("C", "D");

		this.arguments = newArrayList(ARGUMENTS);
		this.command = newArrayList(COMMAND);

		this.mockDescription = mock(BasicDescription.class);
		when(this.mockDescription.executable()).thenReturn(EXECUTABLE);
		when(this.mockDescription.arguments()).thenReturn(this.arguments);
		when(this.mockDescription.command()).thenReturn(this.command);
		when(this.mockDescription.isErrorMappedToOutput()).thenReturn(true);
		when(this.mockDescription.workingDirectory()).thenReturn(FILE);
		when(this.mockDescription.isIOInherited()).thenReturn(false);
		when(this.mockDescription.environment()).thenReturn(this.environment);

		this.description = new ImmutableBasicDescription(this.mockDescription);
	}

	/*
	 * arguments() tests
	 */

	@Test
	public void givenBasicDescription_whenInvokeArguments_expectEqualsConstructedValue() throws Exception {
		/*
		 * When
		 */
		final List<String> actual = this.description.arguments();

		/*
		 * Then
		 */
		assertEquals(ARGUMENTS, actual);
	}

	@Test
	public void givenModifiedArguments_whenInvokeArguments_expectEqualsConstructedValue() throws Exception {
		/*
		 * Given
		 */
		this.arguments.add("Blah blah");

		/*
		 * When
		 */
		final List<String> actual = this.description.arguments();

		/*
		 * Then
		 */
		assertEquals(ARGUMENTS, actual);
	}

	/*
	 * executable() tests
	 */

	@Test
	public void givenBasicDescription_whenInvokeExecutable_ExpectEqualsConstructedValue() throws Exception {
		/*
		 * When
		 */
		final String actual = this.description.executable();

		/*
		 * Then
		 */
		assertEquals(EXECUTABLE, actual);
	}

	/*
	 * command() tests
	 */

	@Test
	public void givenBasicDescription_whenInvokeCommands_expectEqualsConstructedValue() throws Exception {
		/*
		 * When
		 */
		final List<String> actual = this.description.command();

		/*
		 * Then
		 */
		assertEquals(COMMAND, actual);
	}

	/*
	 * isErrorMappedToOutput() tests
	 */

	@Test
	public void givenBasicDescription_whenInvokeIsErrorMappedToOutput_expectEqualsConstructedValue() throws Exception {
		assertTrue(this.description.isErrorMappedToOutput());
	}

	@Test
	public void givenModifiedBasicDescription_whenInvokeIsErrorMappedToOutput_expectEqualsConstructedValue()
			throws Exception {
		/*
		 * Given
		 */
		when(this.mockDescription.isErrorMappedToOutput()).thenReturn(false);

		/*
		 * When
		 */
		final boolean actual = this.description.isErrorMappedToOutput();

		/*
		 * Then
		 */
		assertTrue(actual);
	}

	/*
	 * workingDirectory() tests
	 */

	@Test
	public void givenBasicDescription_whenInvokeWorkingDirectory_expectEqualsConstructedValue() throws Exception {
		/*
		 * When
		 */
		final File value = this.description.workingDirectory();

		/*
		 * Then
		 */
		assertEquals(FILE, value);
	}

	/*
	 * environment() tests
	 */

	@Test
	public void givenBasicDescription_whenInvokeEnvironment_expectEqualsConstructedValue() throws Exception {
		/*
		 * When
		 */
		final Map<String, String> value = this.description.environment();

		/*
		 * Then
		 */
		assertEquals(this.environment, value);
	}

	@Test
	public void givenModifiedEnvironment_whenInvokeEnvironment_expectEqualsConstructedValue() throws Exception {
		final Map<String, String> expected = newHashMap(this.environment);

		/*
		 * Given
		 */
		{
			this.environment.put("X", "Z");
			this.environment.remove("A");
		}

		/*
		 * When
		 */
		final Map<String, String> actual = this.description.environment();

		/*
		 * Then
		 */
		assertEquals(expected, actual);
	}

	/*
	 * isInheritIO() tests
	 */

	@Test
	public void givenModifiedDescription_whenInvokeIsInheritIO_expectEqualsConstructedValue()
			throws Exception {
		/*
		 * Given
		 */
		when(this.mockDescription.isIOInherited()).thenReturn(true);

		/*
		 * When
		 */
		final boolean value = this.description.isIOInherited();

		/*
		 * Then
		 */
		assertFalse(value);
	}

}
