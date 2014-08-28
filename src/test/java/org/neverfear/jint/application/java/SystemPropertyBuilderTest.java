package org.neverfear.jint.application.java;

import static com.google.common.collect.Maps.newHashMap;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neverfear.jint.util.PropertyUtil;

public class SystemPropertyBuilderTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private SystemPropertyBuilder subject;

	@Before
	public void before() {
		this.subject = new SystemPropertyBuilder();
	}

	@Test
	public void givenInitialSystemPropertyBuilder_whenConstructUsingInitialSystemPropertyBuilder_andAddProperties_expectDoesNotModifyInitial() {
		/*
		 * Given
		 */
		final Map<String, String> initialMap = newHashMap();
		initialMap.put("A", "1");
		final SystemPropertyBuilder initialBuilder = new SystemPropertyBuilder(initialMap);

		/*
		 * When
		 */
		final SystemPropertyBuilder subject = new SystemPropertyBuilder(initialBuilder).useUTC();

		/*
		 * Then
		 */
		final Map<String, String> expected = newHashMap();
		expected.put(StandardSystemProperty.TIMEZONE.getName(), "UTC");
		expected.put("A", "1");

		final Map<String, String> actual = subject.build();
		assertEquals(expected, actual);

		assertEquals(1, initialBuilder.build()
				.size());
	}

	@Test
	public void givenInitialMap_whenConstructUsingInitialMap_andAddProperties_expectDoesNotModifyInitialMap() {
		/*
		 * Given
		 */
		final Map<String, String> initialMap = newHashMap();
		initialMap.put("A", "1");

		/*
		 * When
		 */
		final SystemPropertyBuilder subject = new SystemPropertyBuilder(initialMap).useUTC();

		/*
		 * Then
		 */
		final Map<String, String> expected = newHashMap();
		expected.put(StandardSystemProperty.TIMEZONE.getName(), "UTC");
		expected.put("A", "1");

		final Map<String, String> actual = subject.build();
		assertEquals(expected, actual);
		assertEquals(1, initialMap.size());
	}

	@Test
	public void whenUserTimeZoneWithEST_andBuild_expectTimeZoneIsEST() {
		/*
		 * When
		 */
		this.subject.userTimeZone(TimeZone.getTimeZone("EST"));
		final Map<String, String> map = this.subject.build();

		/*
		 * Then
		 */
		final Map<String, String> expected = newHashMap();
		expected.put(StandardSystemProperty.TIMEZONE.getName(), "EST");
		assertEquals(expected, map);
	}

	@Test
	public void whenUseUTC_andBuild_expectTimeZoneIsUTC() {
		/*
		 * When
		 */
		this.subject.useUTC();
		final Map<String, String> map = this.subject.build();

		/*
		 * Then
		 */
		final Map<String, String> expected = newHashMap();
		expected.put(StandardSystemProperty.TIMEZONE.getName(), "UTC");
		assertEquals(expected, map);
	}

	@Test
	public void whenLibraryPath_andBuild_expectJavaLibraryPathSet() {
		/*
		 * When
		 */
		this.subject.libraryPath("A", "B");
		final Map<String, String> map = this.subject.build();

		/*
		 * Then
		 */
		final Map<String, String> expected = newHashMap();
		expected.put(StandardSystemProperty.JAVA_LIBRARY_PATH.getName(), PropertyUtil.LIBRARYPATH_JOINER.join("A", "B"));
		assertEquals(expected, map);
	}

	@Test
	public void whenJmxDisableSsl_andBuild_expectSystemPropertyDisabled() {
		/*
		 * When
		 */
		this.subject.jmxDisableSsl();
		final Map<String, String> map = this.subject.build();

		/*
		 * Then
		 */
		final Map<String, String> expected = newHashMap();
		expected.put(StandardSystemProperty.JMX_REMOTE_SSL_ENABLED.getName(), Boolean.toString(false));
		assertEquals(expected, map);
	}

	@Test
	public void whenJmxDisableAuthenication_andBuild_expectSystemPropertyDisabled() {
		/*
		 * When
		 */
		this.subject.jmxDisableAuthenication();
		final Map<String, String> map = this.subject.build();

		/*
		 * Then
		 */
		final Map<String, String> expected = newHashMap();
		expected.put(StandardSystemProperty.JMX_REMOTE_AUTHENTICATION_ENABLED.getName(), Boolean.toString(false));
		assertEquals(expected, map);
	}

	@Test
	public void givenValidPort_whenJmxRemotePort_andBuild_expectJmxEnabled_andPortSet() {
		/*
		 * Given
		 */
		final int validPort = 123;

		/*
		 * When
		 */
		this.subject.jmxRemotePort(validPort);
		final Map<String, String> map = this.subject.build();

		/*
		 * Then
		 */
		final Map<String, String> expected = newHashMap();
		expected.put(StandardSystemProperty.JMX_REMOTE_ENABLED.getName(), Boolean.toString(true));
		expected.put(StandardSystemProperty.JMX_REMOTE_PORT.getName(), Integer.toString(validPort));
		assertEquals(expected, map);
	}

	@Test
	public void givenInvalidPort_whenJmxRemotePort_expectIllegalArgumentException() {
		/*
		 * Then
		 */
		this.expectedException.expect(IllegalArgumentException.class);

		/*
		 * Given
		 */
		this.subject.jmxRemotePort(-1);

	}

	@Test
	public void givenProperties_whenLoadFromProperties_andBuild_andModifyProperties_expectLoadedValuesCopiedOver() {
		/*
		 * Given
		 */
		final Properties properties = new Properties();
		properties.put("A", "1");

		/*
		 * When
		 */
		this.subject.load(properties);
		properties.put("B", "2");
		final Map<String, String> map = this.subject.build();

		/*
		 * Then
		 */
		final Map<String, String> expected = newHashMap();
		expected.put("A", "1");
		assertEquals(expected, map);
	}

	@Test
	public void givenValidResource_whenLoadFromResources_andBuild_expectLoadedValuesCopiedOver() throws IOException {
		/*
		 * When
		 */
		this.subject.loadResource("SystemPropertyBuilderTest.properties");
		final Map<String, String> map = this.subject.build();

		/*
		 * Then
		 */
		final Map<String, String> expected = newHashMap();
		expected.put("A", "1");
		assertEquals(expected, map);
	}

	@Test
	public void givenMissingResource_whenLoadFromResources_expectFileNotFoundException() throws IOException {
		/*
		 * Then
		 */
		this.expectedException.expect(FileNotFoundException.class);

		/*
		 * When
		 */
		this.subject.loadResource("i.dont.exist");
	}

	@Test
	public void givenValidFile_whenLoadFromFile_andBuild_expectLoadedValuesCopiedOver() throws IOException {
		/*
		 * When
		 */
		this.subject.loadFile(new File("src/test/resources/SystemPropertyBuilderTest.properties"));
		final Map<String, String> map = this.subject.build();

		/*
		 * Then
		 */
		final Map<String, String> expected = newHashMap();
		expected.put("A", "1");
		assertEquals(expected, map);
	}

	@Test
	public void givenMissingFile_whenLoadFromFile_expectFileNotFoundException() throws IOException {
		/*
		 * Then
		 */
		this.expectedException.expect(FileNotFoundException.class);

		/*
		 * When
		 */
		this.subject.loadFile(new File("i.dont.exist"));
	}

	@Test
	public void whenLogbackConfigFile_expectLogbackConfigSet() {
		/*
		 * When
		 */
		this.subject.logbackConfigFile("A");
		final Map<String, String> map = this.subject.build();

		/*
		 * Then
		 */
		final Map<String, String> expected = newHashMap();
		expected.put(CommonSystemProperty.LOGBACK_CONFIG_FILE.getName(), "A");
		assertEquals(expected, map);
	}

	@Test
	public void whenLog4jConfigFile_expectLog4jConfigSet() {
		/*
		 * When
		 */
		this.subject.log4jConfigFile("A");
		final Map<String, String> map = this.subject.build();

		/*
		 * Then
		 */
		final Map<String, String> expected = newHashMap();
		expected.put(CommonSystemProperty.LOG4J_CONFIG_FILE.getName(), "A");
		assertEquals(expected, map);
	}

	@Test
	public void whenJavaUtilLoggingConfigFile_expectJavaUtilLoggingConfigSet() {
		/*
		 * When
		 */
		this.subject.julConfigFile("A");
		final Map<String, String> map = this.subject.build();

		/*
		 * Then
		 */
		final Map<String, String> expected = newHashMap();
		expected.put(CommonSystemProperty.JUL_CONFIG_FILE.getName(), "A");
		assertEquals(expected, map);
	}

}
