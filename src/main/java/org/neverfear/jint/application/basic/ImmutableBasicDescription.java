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

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

import org.neverfear.jint.application.ImmutableDescription;

public class ImmutableBasicDescription
	extends ImmutableDescription
	implements BasicDescription {

	private static final long serialVersionUID = 6367237110098726966L;

	private final String executable;
	private final List<String> arguments;

	public ImmutableBasicDescription(final BasicDescription description) {
		super(description);
		this.executable = description.executable();
		this.arguments = unmodifiableList(newArrayList(description.arguments()));
	}

	@Override
	public final String executable() {
		return this.executable;
	}

	@Override
	public final List<String> arguments() {
		return this.arguments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.arguments == null) ? 0 : this.arguments.hashCode());
		result = prime * result + ((this.executable == null) ? 0 : this.executable.hashCode());
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
		final ImmutableBasicDescription other = (ImmutableBasicDescription) obj;
		if (this.arguments == null) {
			if (other.arguments != null) {
				return false;
			}
		} else if (!this.arguments.equals(other.arguments)) {
			return false;
		}
		if (this.executable == null) {
			if (other.executable != null) {
				return false;
			}
		} else if (!this.executable.equals(other.executable)) {
			return false;
		}
		return true;
	}

}
