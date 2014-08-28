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
package org.neverfear.jint.matcher;

import java.util.Collection;

import org.hamcrest.Matcher;

/**
 * @author doug@neverfear.org
 * 
 */
public final class JintMatchers {

	private JintMatchers() {
		throw new AssertionError();
	}

	public static RootCauseFeatureMatcher rootCause(final Matcher<? super Throwable> matcher) {
		return new RootCauseFeatureMatcher(matcher);
	}

	public static CausedByMatcher causedBy(final Throwable cause) {
		return new CausedByMatcher(cause);
	}

	public static SurpressedFeatureMatcher surpressed(final Matcher<? super Collection<Throwable>> matcher) {
		return new SurpressedFeatureMatcher(matcher);
	}
}
