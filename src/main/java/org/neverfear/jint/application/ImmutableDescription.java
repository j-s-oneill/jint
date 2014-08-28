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
package org.neverfear.jint.application;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.neverfear.jint.api.Description;

/**
 * Intended to be extended but is a stand-alone, bare-bones implementation of a
 * {@link Description}
 * 
 * @author doug@neverfear.org
 * 
 */
public class ImmutableDescription
	implements Description, Serializable {

	private static final long serialVersionUID = 8431152177108675159L;

	private final File workingDirectory;
	private final Map<String, String> environment;
	private final List<String> command;
	private final boolean inheritIO;
	private final boolean errorMappedToOutput;

	/**
	 * Arguments are strictly copied to allow subsequent mutations after
	 * construction of the snapshot. This is useful for the builders.
	 * 
	 * @param description
	 */
	public ImmutableDescription(final Description description) {
		this(description.workingDirectory(),
				description.environment(),
				description.command(),
				description.isIOInherited(),
				description.isErrorMappedToOutput());
	}

	/**
	 * Arguments are strictly copied to allow subsequent mutations after
	 * construction of the snapshot. This is useful for the builders.
	 * 
	 * @param workingDirectory
	 * @param environment
	 * @param command
	 * @param redirectStandardInput
	 * @param redirectStandardOutput
	 * @param redirectStandardError
	 * @param errorMappedToOutput
	 */
	public ImmutableDescription(final File workingDirectory,
			final Map<String, String> environment,
			final List<String> command,
			final boolean inheritIO,
			final boolean errorMappedToOutput) {
		super();
		/*
		 * File is non-final and so could be subject to mutation. To encapsulate
		 * this correctly we produce a copy using the stored path name.
		 */
		this.workingDirectory = new File(workingDirectory.getPath());
		this.environment = newHashMap(environment);
		this.command = newArrayList(command);

		this.inheritIO = inheritIO;
		this.errorMappedToOutput = errorMappedToOutput;
	}

	@Override
	public File workingDirectory() {
		return this.workingDirectory;
	}

	@Override
	public Map<String, String> environment() {
		return unmodifiableMap(this.environment);
	}

	@Override
	public List<String> command() {
		return unmodifiableList(this.command);
	}

	@Override
	public boolean isErrorMappedToOutput() {
		return this.errorMappedToOutput;
	}

	@Override
	public boolean isIOInherited() {
		return this.inheritIO;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.command == null) ? 0 : this.command.hashCode());
		result = prime * result + ((this.environment == null) ? 0 : this.environment.hashCode());
		result = prime * result + (this.errorMappedToOutput ? 1231 : 1237);
		result = prime * result + (this.inheritIO ? 1231 : 1237);
		result = prime * result + ((this.workingDirectory == null) ? 0 : this.workingDirectory.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ImmutableDescription other = (ImmutableDescription) obj;
		if (this.command == null) {
			if (other.command != null) {
				return false;
			}
		} else if (!this.command.equals(other.command)) {
			return false;
		}
		if (this.environment == null) {
			if (other.environment != null) {
				return false;
			}
		} else if (!this.environment.equals(other.environment)) {
			return false;
		}
		if (this.errorMappedToOutput != other.errorMappedToOutput) {
			return false;
		}
		if (this.inheritIO != other.inheritIO) {
			return false;
		}
		if (this.workingDirectory == null) {
			if (other.workingDirectory != null) {
				return false;
			}
		} else if (!this.workingDirectory.equals(other.workingDirectory)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()
				+ " [workingDirectory="
				+ this.workingDirectory + ", environment=" + this.environment + ", command=" + this.command
				+ ", inheritIO=" + this.inheritIO + ", errorMappedToOutput=" + this.errorMappedToOutput + "]";
	}

}
