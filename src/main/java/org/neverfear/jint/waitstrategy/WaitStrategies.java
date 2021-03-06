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
package org.neverfear.jint.waitstrategy;

import java.util.regex.Pattern;

import javax.management.ObjectName;

import org.neverfear.jint.api.WaitStrategy;

/**
 * @author doug@neverfear.org
 * 
 */
public final class WaitStrategies {

	private WaitStrategies() {
		throw new AssertionError();
	}

	public static WaitStrategy forJmxBean(final ObjectName beanName, final String hostname, final int port) {
		return new UntilHealthyWaitStrategy(new JmxBeanRegistrationHealthCheck(beanName,
				hostname,
				port));
	}

	public static WaitStrategy forTcpPort(final int port) {
		return new UntilHealthyWaitStrategy(new TcpPortHealthCheck(port));
	}

	public static WaitStrategy consoleMatches(final String regex) {
		return consoleMatches(Pattern.compile(regex));
	}

	public static WaitStrategy consoleMatches(final Pattern pattern) {
		return new ConsoleWaitStrategy(pattern);
	}
}
