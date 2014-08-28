package org.neverfear.jint.application;

import static com.google.common.collect.Maps.newHashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.neverfear.jint.api.Description;

import com.google.common.collect.Lists;

public class ImmutableDescriptionTest {

	private static final File FILE = new File("Some file path");

	private final File mockWorkingDirectory = FILE;
	private Map<String, String> environment;
	private List<String> command;
	private final boolean errorMappedToOutput = false;

	private ImmutableDescription subject;

	@Before
	public void before() {
		this.environment = newHashMap();
		this.command = Lists.newArrayList("A", "B");

		this.subject = new ImmutableDescription(this.mockWorkingDirectory,
				this.environment,
				this.command,
				true,
				this.errorMappedToOutput);
	}

	@Test
	public void givenDescription_whenInvokeDescriptionConstructor_expectInvokesEachAccessorOnOperand() {
		/*
		 * Given
		 */
		final Description description = Mockito.spy(this.subject);

		/*
		 * When
		 */
		final Description newDescription = new ImmutableDescription(description);

		/*
		 * Then
		 */
		{
			verify(description).workingDirectory();
			verify(description).environment();
			verify(description).command();
			verify(description).isIOInherited();
			verify(description).isErrorMappedToOutput();
			assertEquals(this.subject, newDescription);
		}
	}

	@Test
	public void givenImmutableDescription_whenSerialize_andDeserialize_expectLogicallyEqual() throws Exception {
		/*
		 * Given
		 */
		final Description description = new ImmutableDescription(this.subject);
		final ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		final ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);

		/*
		 * When
		 */
		objectOutput.writeObject(description);

		final ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(byteOutput.toByteArray()));
		final Description resultant = (Description) objectInput.readObject();

		/*
		 * Then
		 */
		assertFalse("Expected different object instance", resultant == description);
		assertEquals(resultant, description);

	}

	@Test(expected = UnsupportedOperationException.class)
	public void givenImmutableDescription_whenMutateCommand_expectUnsupportedOperationException() {
		this.subject.command()
				.clear();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void givenImmutableDescription_whenMutateEnvironment_expectUnsupportedOperationException() {
		this.subject.environment()
				.clear();
	}

}
