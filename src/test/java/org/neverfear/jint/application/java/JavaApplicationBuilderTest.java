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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import applications.EchoMain;

import com.google.common.base.Joiner;

/**
 * @author doug@neverfear.org
 * 
 */
public class JavaApplicationBuilderTest {

	private static final String OUTER_CLASS_CANONICAL_NAME = JavaApplicationBuilderTest.class.getCanonicalName();

	public static final class StaticInner {

		private static final String STATIC_INNER_INNER_SIMPLE_NAME = StaticInnerInner.class.getSimpleName();

		private static final class StaticInnerInner {

			@SuppressWarnings("unused")
			public static void main(final String[] strings) {
				System.out.println("Inner inner");
			}
		}

		public static void main(final String[] strings) {
			System.out.println("Inner");
		}
	}

	private static final String STATIC_INNER_SIMPLE_NAME = StaticInner.class.getSimpleName();

	private JavaApplicationBuilder subject;

	@Before
	public void before() {
		this.subject = new JavaApplicationBuilder();
	}

	@Test
	public void givenTopLevelClass_whenMainClass_expectMainClassNameIsClassCanonicalName() {
		this.subject.mainClass(EchoMain.class);
		assertEquals(EchoMain.class.getCanonicalName(), this.subject.mainClassName());
	}

	@Test
	public void givenStaticInnerClass_whenMainClass_expectMainClassNameContainsDollar() {
		this.subject.mainClass(StaticInner.class);
		assertEquals(OUTER_CLASS_CANONICAL_NAME + "$" + STATIC_INNER_SIMPLE_NAME,
			this.subject.mainClassName());
	}

	@Test
	public void givenStaticInnerInnerClass_whenMainClass_expectMainClassNameContainsDollar() {
		this.subject.mainClass(StaticInner.StaticInnerInner.class);
		assertEquals(Joiner.on("$")
				.join(OUTER_CLASS_CANONICAL_NAME, STATIC_INNER_SIMPLE_NAME, StaticInner.STATIC_INNER_INNER_SIMPLE_NAME),
			this.subject.mainClassName());
	}

}
