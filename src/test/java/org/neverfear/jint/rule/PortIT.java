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
package org.neverfear.jint.rule;

import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.internal.matchers.GreaterOrEqual;
import org.mockito.internal.matchers.LessOrEqual;

/**
 * @author doug@neverfear.org
 * 
 */
public class PortIT {

	@Rule
	public Port anyPort = Port.any();

	@Rule
	public Port ranged = Port.withinRange(10000, 40000);

	@Test
	public void anyPortShouldBeGreaterThanZeroAndLessThanMax() {
		assertThat("Port must be greater than or equal to the lowest bindable port",
			this.anyPort.get(),
			new GreaterOrEqual<>(Port.LOWEST_VALID_PORT));
		assertThat("Port must be less than or equal the highest bindable port",
			this.anyPort.get(),
			new LessOrEqual<>(Port.HIGHEST_PORT));
	}

	@Test
	public void rangedPortShouldBeWithinRange() {
		assertThat("Port must be greater than or equal to the lowest bindable port",
			this.ranged.get(),
			new GreaterOrEqual<>(10000));
		assertThat("Port must be less than or equal the highest bindable port",
			this.ranged.get(),
			new LessOrEqual<>(40000));
	}
}
