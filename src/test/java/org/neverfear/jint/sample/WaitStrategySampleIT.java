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
package org.neverfear.jint.sample;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.neverfear.jint.api.Jint;
import org.neverfear.jint.application.java.JavaApplication;
import org.neverfear.jint.waitstrategy.ConsoleWaitStrategy;

import applications.EchoMain;

/**
 * Demonstrates console interactions.
 * 
 * @author doug@neverfear.org
 * 
 */
public class WaitStrategySampleIT {

	@Rule
	public TestRule timeout = new DisableOnDebug(Timeout.seconds(1));

	private final JavaApplication echoMain = Jint.java(EchoMain.class)
			.arguments("this application will print this string out when it starts")
			.waitStrategy(new ConsoleWaitStrategy("print this string"))
			.build();

	@Before
	public void before() throws Exception {
		this.echoMain.start();
	}

	@After
	public void after() throws Exception {
		this.echoMain.stop();
		this.echoMain.awaitStop();
	}

	/**
	 * This is the simplest case
	 */
	@Test
	public void givenMatches_whenAwaitStart_expectDoneBeforeTestTimeout() throws Exception {
		this.echoMain.awaitStart();
	}

}
