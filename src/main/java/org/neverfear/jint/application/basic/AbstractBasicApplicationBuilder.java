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
package org.neverfear.jint.application.basic;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.unmodifiableList;
import static org.neverfear.jint.util.PropertyUtil.currentEnvironment;
import static org.neverfear.jint.util.PropertyUtil.currentWorkingDirectory;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.neverfear.jint.api.ApplicationBuilder;
import org.neverfear.jint.api.Handle;
import org.neverfear.jint.api.Location;
import org.neverfear.jint.api.WaitStrategy;
import org.neverfear.jint.location.local.Localhost;
import org.neverfear.jint.util.CollectionUtil;
import org.neverfear.jint.util.ProcessUtil;
import org.neverfear.jint.waitstrategy.NoOpWaitStrategy;

@SuppressWarnings("unchecked")
public abstract class AbstractBasicApplicationBuilder<A extends BasicApplication, B extends ApplicationBuilder>
	implements BasicDescription, ApplicationBuilder {

	protected String executable = defaultExecutable();
	protected List<String> arguments = defaultArguments();
	protected File workingDirectory = defaultWorkingDirectory();
	protected Map<String, String> environment = defaultEnvironment();

	protected boolean inheritIO = defaultInheritIO();
	protected boolean errorMappedToOutput = defaultErrorMappedToOutput();

	protected WaitStrategy waitStrategy = defaultWaitStrategy();
	protected Location location = defaultLocation();

	/*
	 * Defaults
	 */

	protected String defaultExecutable() {
		return null;
	}

	protected List<String> defaultArguments() {
		return newArrayList();
	}

	protected File defaultWorkingDirectory() {
		return currentWorkingDirectory();
	}

	protected Map<String, String> defaultEnvironment() {
		return newHashMap(currentEnvironment());
	}

	protected boolean defaultInheritIO() {
		return false;
	}

	protected boolean defaultErrorMappedToOutput() {
		return false;
	}

	protected WaitStrategy defaultWaitStrategy() {
		return NoOpWaitStrategy.INSTANCE;
	}

	protected Location defaultLocation() {
		return new Localhost();
	}

	/*
	 * Getters
	 */

	@Override
	public List<String> command() {
		final List<String> command = newArrayListWithCapacity(this.arguments.size() + 1);
		command.add(this.executable);
		command.addAll(this.arguments);
		return unmodifiableList(command);
	}

	/*
	 * Simples fields
	 */

	public B waitStrategy(final WaitStrategy waitStrategy) {
		this.waitStrategy = waitStrategy;
		return (B) this;
	}

	@Override
	public boolean isIOInherited() {
		return this.inheritIO;
	}

	/**
	 * Causes the application to inherit it's standard output, error and input
	 * from the parent process.
	 * 
	 * @return
	 */
	public B inheritIO() {
		this.inheritIO = true;
		return (B) this;
	}

	@Override
	public String executable() {
		return this.executable;
	}

	public B executable(final String executable) {
		checkNotNull(executable, "executable cannot be null");
		this.executable = executable;
		return (B) this;
	}

	@Override
	public File workingDirectory() {
		return this.workingDirectory;
	}

	public B workingDirectory(final File workingDirectory) {
		checkNotNull(workingDirectory, "working directory cannot be null");
		this.workingDirectory = workingDirectory;
		return (B) this;
	}

	@Override
	public boolean isErrorMappedToOutput() {
		return this.errorMappedToOutput;
	}

	public B errorMappedToOutput(final boolean errorMappedToOutput) {
		this.errorMappedToOutput = errorMappedToOutput;
		return (B) this;
	}

	/*
	 * Collections
	 */

	@Override
	public List<String> arguments() {
		return this.arguments;
	}

	public B mergeArguments(final List<String> arguments) {
		checkNotNull(arguments, "arguments cannot be null");
		CollectionUtil.merge(this.arguments, arguments);
		return (B) this;
	}

	public B mergeArguments(final String... arguments) {
		checkNotNull(arguments, "arguments cannot be null");
		CollectionUtil.merge(this.arguments, arguments);
		return (B) this;
	}

	public B arguments(final String... arguments) {
		checkNotNull(arguments, "arguments cannot be null");
		CollectionUtil.replace(this.arguments, arguments);
		return (B) this;
	}

	public B arguments(final List<String> arguments) {
		checkNotNull(arguments, "arguments cannot be null");
		CollectionUtil.replace(this.arguments, arguments);
		return (B) this;
	}

	/*
	 * Maps
	 */

	@Override
	public Map<String, String> environment() {
		return this.environment;
	}

	/**
	 * Defines a single environment variable that will be merged into the
	 * existing environment.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public B environment(final String key, final String value) {
		checkNotNull(key, "key cannot be null");
		checkNotNull(value, "value cannot be null");
		CollectionUtil.merge(this.environment, key, value);
		return (B) this;
	}

	public B mergeEnvironment(final Map<String, String> environment) {
		checkNotNull(environment, "environment cannot be null");
		CollectionUtil.merge(this.environment, environment);
		return (B) this;
	}

	public B environment(final Map<String, String> environment) {
		checkNotNull(environment, "environment cannot be null");
		CollectionUtil.replace(this.environment, environment);
		return (B) this;
	}

	public Location location() {
		return this.location;
	}

	public B location(final Location location) {
		checkNotNull(location, "location cannot be null");
		this.location = location;
		return (B) this;
	}

	/**
	 * <p>
	 * Use this method to validate anything that must be set or where the
	 * default value is unsuitable.
	 * </p>
	 * <p>
	 * Designed to be overridden by subclasses. Subclasses should remember to
	 * call super.validate();
	 * </p>
	 */
	protected void validate() {
		checkNotNull(this.executable, "executable not set");
	}

	protected abstract A toApplication(final Handle handle);

	@Override
	public A build() {
		validate();
		final Handle handle = this.location.create(this);
		final A application = toApplication(handle);
		return application;
	}

	@Override
	public String toString() {
		return getClass().getName() + " [command=" + ProcessUtil.toCommand(command()) + "]";
	}
}
