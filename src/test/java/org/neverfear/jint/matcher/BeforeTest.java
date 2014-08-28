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

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;

import org.hamcrest.StringDescription;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author doug@neverfear.org
 * 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BeforeTest {

	private static final Before<String> oneBeforeTwo = new Before<>("one",
			"two");

	@Test
	public void whenDescription_expectOneBeforeTwo() {
		final StringDescription description = new StringDescription();
		oneBeforeTwo.describeTo(description);
		assertEquals("\"one\" before \"two\"", description.toString());
	}

	@Test
	public void givenTwoThenOne_whenDescribeMismatch_expectTwoIsBeforeOne() {
		final StringDescription description = new StringDescription();
		oneBeforeTwo.describeMismatch(Arrays.asList("two", "one"), description);
		assertEquals("\"two\" is before \"one\"", description.toString());
	}

	@Test
	public void givenTwo_whenDescribeMismatch_expectTwoIsFoundButOneIsNotInTwo() {
		final StringDescription description = new StringDescription();
		oneBeforeTwo.describeMismatch(Arrays.asList("two"), description);
		assertEquals("\"one\" is not in <[two]>", description.toString());
	}

	@Test
	public void givenOne_whenDescribeMismatch_expectOneIsFoundButTwoIsNotInOne() {
		final StringDescription description = new StringDescription();
		oneBeforeTwo.describeMismatch(Arrays.asList("one"), description);
		assertEquals("\"one\" was found but \"two\" is not in <[one]>", description.toString());
	}

	@Test
	public void whenOneThenTwo_expectMatches() {
		assertThat(Arrays.asList("one", "two"), oneBeforeTwo);
	}

	@Test
	public void whenOneThenOtherThenTwo_expectMatches() {
		assertThat(Arrays.asList("one", "other", "two"), oneBeforeTwo);
	}

	@Test
	public void whenTwoThenOne_expectDoesNotMatch() {
		assertThat(Arrays.asList("two", "one"), not(oneBeforeTwo));
	}

	@Test
	public void whenTwo_expectDoesNotMatch() {
		assertThat(Arrays.asList("two"), not(oneBeforeTwo));
	}

	@Test
	public void whenOne_expectDoesNOtMatch() {
		assertThat(Arrays.asList("one"), not(oneBeforeTwo));
	}

	@Test
	public void whenOther_expectDoesNotMatch() {
		assertThat(Arrays.asList("other"), not(oneBeforeTwo));
	}

	@Test
	public void whenEmpty_expectDoesNotMatch() {
		assertThat(Collections.<String> emptyList(), not(oneBeforeTwo));
	}

}
