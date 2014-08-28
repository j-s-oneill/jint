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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.unmodifiableList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neverfear.jint.api.ApplicationBuilder;
import org.neverfear.jint.application.basic.AbstractBasicApplicationBuilder;
import org.neverfear.jint.resource.PortSupplier;
import org.neverfear.jint.util.CollectionUtil;
import org.neverfear.jint.util.RuntimeUtil;

import com.google.common.base.Joiner;

@SuppressWarnings("unchecked")
public abstract class AbstractJavaApplicationBuilder<A extends JavaApplication, B extends ApplicationBuilder>
	extends AbstractBasicApplicationBuilder<A, B>
	implements JavaDescription {

	private static final Joiner PATH_SEP_JOINER = Joiner.on(File.pathSeparator);
	private static final Joiner COMMA_JOINER = Joiner.on(',');

	public static final String JAVA_EXECUTABLE = "java";

	protected String mainClassName = defaultMainClassName();
	protected final List<String> classPath = defaultClassPath();
	protected final List<String> jvmArguments = defaultJvmArguments();
	protected final Map<String, String> systemProperties = defaultSystemProperties();

	protected int remoteDebugPort;

	/*
	 * Defaults
	 */

	protected List<String> defaultClassPath() {
		return newArrayList(RuntimeUtil.classPath());
	}

	@Override
	protected String defaultExecutable() {
		return JAVA_EXECUTABLE;
	}

	protected List<String> defaultJvmArguments() {
		return newArrayList();
	}

	protected Map<String, String> defaultSystemProperties() {
		return newHashMap();
	}

	protected String defaultMainClassName() {
		return null;
	}

	@Override
	public List<String> command() {
		final List<String> command = new ArrayList<>();

		command.add(executable());
		command.add("-cp");
		command.add(PATH_SEP_JOINER
				.join(classPath()));

		for (final String jvmArgument : jvmArguments()) {
			command.add(jvmArgument);
		}

		for (final Map.Entry<String, String> entry : systemProperties().entrySet()) {
			final String value = entry.getValue();
			if (value != null) {
				command.add("-D" + entry.getKey() + "=" + value);
			} else {
				command.add("-D" + entry.getKey());
			}
		}

		command.add(mainClassName());

		for (final String argument : arguments()) {
			command.add(argument);
		}

		return unmodifiableList(command);
	}

	/*
	 * Simple fields
	 */

	/**
	 * This may be called to specify the java installation where the java binary
	 * is not present on the system PATH.
	 */
	@Override
	public B executable(final String executable) {
		return super.executable(executable);
	}

	@Override
	public String mainClassName() {
		return this.mainClassName;
	}

	public B mainClassName(final String mainClassName) {
		checkNotNull(mainClassName, "main-class cannot be null");
		checkArgument(!mainClassName.isEmpty(), "main-class cannot be an empty");
		this.mainClassName = mainClassName;
		return (B) this;
	}

	@Override
	public Class<?> mainClass() throws ClassNotFoundException {
		return Class.forName(this.mainClassName);
	}

	public B mainClass(final Class<?> mainClass) {
		checkNotNull(mainClass, "main-class cannot be null");
		this.mainClassName = mainClass.getName();
		return (B) this;
	}

	/*
	 * Collections
	 */

	@Override
	public List<String> classPath() {
		return this.classPath;
	}

	public B classPath(final List<String> classPath) {
		checkNotNull(classPath, "class path cannot be null");
		checkArgument(!classPath.isEmpty(), "class path cannot be empty");
		CollectionUtil.replace(this.classPath, classPath);
		return (B) this;
	}

	public B classPath(final String... classPath) {
		checkNotNull(classPath, "class path cannot be null");
		checkArgument(classPath.length > 0, "class path cannot be empty");
		CollectionUtil.replace(this.classPath, classPath);
		return (B) this;
	}

	public B mergeClassPath(final List<String> classPath) {
		checkNotNull(classPath, "class path cannot be null");
		checkArgument(!classPath.isEmpty(), "class path cannot be empty");
		CollectionUtil.merge(this.classPath, classPath);
		return (B) this;
	}

	public B mergeClassPath(final String... classPath) {
		checkNotNull(classPath, "class path cannot be null");
		checkArgument(classPath.length > 0, "class path cannot be empty");
		CollectionUtil.merge(this.classPath, classPath);
		return (B) this;
	}

	@Override
	public List<String> jvmArguments() {
		return this.jvmArguments;
	}

	public B jvmArguments(final List<String> jvmArguments) {
		checkNotNull(jvmArguments, "jvm arguments cannot be null");
		CollectionUtil.replace(this.jvmArguments, jvmArguments);
		return (B) this;
	}

	public B jvmArguments(final String... jvmArguments) {
		checkNotNull(jvmArguments, "jvm arguments cannot be null");
		CollectionUtil.replace(this.jvmArguments, jvmArguments);
		return (B) this;
	}

	public B mergeJvmArguments(final List<String> jvmArguments) {
		checkNotNull(jvmArguments, "jvm arguments cannot be null");
		CollectionUtil.merge(this.jvmArguments, jvmArguments);
		return (B) this;
	}

	public B mergeJvmArguments(final String... jvmArguments) {
		checkNotNull(jvmArguments, "jvm arguments cannot be null");
		CollectionUtil.merge(this.jvmArguments, jvmArguments);
		return (B) this;
	}

	/*
	 * Maps
	 */

	@Override
	public Map<String, String> systemProperties() {
		return this.systemProperties;
	}

	public B systemProperty(final String key, final String value) {
		checkNotNull(key, "system property name cannot be null");
		checkNotNull(value, "system property value cannot be null");
		CollectionUtil.merge(this.systemProperties, key, value);
		return (B) this;
	}

	public B mergeSystemProperties(final Map<String, String> systemProperties) {
		checkNotNull(systemProperties, "system properties cannot be null");
		CollectionUtil.merge(this.systemProperties, systemProperties);
		return (B) this;
	}

	public B systemProperties(final Map<String, String> systemProperties) {
		checkNotNull(systemProperties, "system properties cannot be null");
		CollectionUtil.replace(this.systemProperties, systemProperties);
		return (B) this;
	}

	public B systemProperties(final SystemPropertyBuilder systemProperties) {
		checkNotNull(systemProperties, "system properties cannot be null");
		systemProperties(systemProperties.build());
		return (B) this;

	}

	private static void remoteDebugArguments(final List<String> target, final String address,
			final boolean server,
			final boolean suspend) {
		/*
		 * Use -Xdebug rather than agentlib:jdwp because it supports more JVM's
		 * and I've not found any disadvantages.
		 */
		target.add("-Xdebug");

		final List<String> options = newArrayList();
		options.add("transport=dt_socket");
		if (server) {
			options.add("server=y");
		} else {
			options.add("server=n");
		}

		if (suspend) {
			options.add("suspend=y");
		} else {
			options.add("suspend=n");
		}

		options.add("address=" + address);

		target.add("-Xrunjdwp:" + COMMA_JOINER.join(options));
	}

	public B remoteDebugServer(final int port, final boolean suspend) {
		remoteDebugArguments(this.jvmArguments, Integer.toString(port), true, suspend);
		this.remoteDebugPort = port;
		return (B) this;
	}

	public B remoteDebugClient(final String hostname, final int port, final boolean suspend) {
		remoteDebugArguments(this.jvmArguments, hostname + ":" + port, false, suspend);
		this.remoteDebugPort = port;
		return (B) this;
	}

	public int remoteDebugPort() {
		return this.remoteDebugPort;
	}

	/**
	 * <p>
	 * Enable the remote debug server on the java application automatically if
	 * the java process that launched it is currently being debugged.
	 * </p>
	 * 
	 * @return
	 */
	public B remoteDebugServerAuto() {
		if (RuntimeUtil.isDebug()) {
			final int port;
			try {
				port = PortSupplier.randomPort();
			} catch (final IOException e) {
				throw new IllegalStateException("Failed to allocate debug port",
						e);
			}
			return remoteDebugServer(port, true);
		} else {
			return (B) this;
		}
	}

	@Override
	protected void validate() {
		super.validate();
		checkNotNull(this.mainClassName, "main-class not set");
	}

}
