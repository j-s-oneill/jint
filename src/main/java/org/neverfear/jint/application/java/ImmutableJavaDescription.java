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

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.util.List;
import java.util.Map;

import org.neverfear.jint.application.basic.ImmutableBasicDescription;

public class ImmutableJavaDescription
	extends ImmutableBasicDescription
	implements JavaDescription {

	private static final long serialVersionUID = -3598331544680308340L;

	private final String mainClassName;

	private final List<String> classPath;
	private final List<String> jvmArguments;
	private final Map<String, String> systemProperties;

	public ImmutableJavaDescription(final JavaDescription description) {
		super(description);
		this.mainClassName = description.mainClassName();
		this.classPath = unmodifiableList(description.classPath());
		this.jvmArguments = unmodifiableList(description.jvmArguments());
		this.systemProperties = unmodifiableMap(description.systemProperties());
	}

	@Override
	public final String mainClassName() {
		return this.mainClassName;
	}

	@Override
	public final List<String> classPath() {
		return this.classPath;
	}

	@Override
	public final List<String> jvmArguments() {
		return this.jvmArguments;
	}

	@Override
	public final Map<String, String> systemProperties() {
		return this.systemProperties;
	}

	@Override
	public final Class<?> mainClass() throws ClassNotFoundException {
		return Class.forName(this.mainClassName);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.classPath == null) ? 0 : this.classPath.hashCode());
		result = prime * result + ((this.jvmArguments == null) ? 0 : this.jvmArguments.hashCode());
		result = prime * result + ((this.mainClassName == null) ? 0 : this.mainClassName.hashCode());
		result = prime * result + ((this.systemProperties == null) ? 0 : this.systemProperties.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ImmutableJavaDescription other = (ImmutableJavaDescription) obj;
		if (this.classPath == null) {
			if (other.classPath != null) {
				return false;
			}
		} else if (!this.classPath.equals(other.classPath)) {
			return false;
		}
		if (this.jvmArguments == null) {
			if (other.jvmArguments != null) {
				return false;
			}
		} else if (!this.jvmArguments.equals(other.jvmArguments)) {
			return false;
		}
		if (this.mainClassName == null) {
			if (other.mainClassName != null) {
				return false;
			}
		} else if (!this.mainClassName.equals(other.mainClassName)) {
			return false;
		}
		if (this.systemProperties == null) {
			if (other.systemProperties != null) {
				return false;
			}
		} else if (!this.systemProperties.equals(other.systemProperties)) {
			return false;
		}
		return true;
	}

}
