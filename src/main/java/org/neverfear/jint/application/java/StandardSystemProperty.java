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

/**
 * System properties intended to be set on start up that are part of the
 * standard JRE.
 * 
 * @author doug@neverfear.org
 * 
 */
public enum StandardSystemProperty {

	/**
	 * Time zone to use. Uses the system time zone by default.
	 */
	TIMEZONE("user.timezone"),

	/**
	 * Path used to find native libraries. Elements of the java library path are
	 * separated by a platform-specific character specified in the
	 * path.separator property.
	 */
	JAVA_LIBRARY_PATH("java.library.path"),

	/**
	 * Enabled by default.
	 * 
	 * @see <a
	 *      href="http://docs.oracle.com/javase/8/docs/technotes/guides/management/agent.html#gdevf">Out-of-the-Box
	 *      Monitoring and Management Properties</a>
	 */
	JMX_REMOTE_ENABLED("com.sun.management.jmxremote"),

	/**
	 * Port for the JMX remote agent to listen upon.
	 * 
	 * @see <a
	 *      href="http://docs.oracle.com/javase/8/docs/technotes/guides/management/agent.html#gdevf">Out-of-the-Box
	 *      Monitoring and Management Properties</a>
	 */
	JMX_REMOTE_PORT("com.sun.management.jmxremote.port"),

	/**
	 * Enabled by default.
	 * 
	 * @see <a
	 *      href="http://docs.oracle.com/javase/8/docs/technotes/guides/management/agent.html#gdevf">Out-of-the-Box
	 *      Monitoring and Management Properties</a>
	 */
	JMX_REMOTE_SSL_ENABLED("com.sun.management.jmxremote.ssl"),

	/**
	 * Defaults to SSL/TLS.
	 * 
	 * @see <a
	 *      href="http://docs.oracle.com/javase/8/docs/technotes/guides/management/agent.html#gdevf">Out-of-the-Box
	 *      Monitoring and Management Properties</a>
	 */
	JMX_REMOTE_SSL_ENABLED_PROTOCOLS("com.sun.management.jmxremote.ssl.enabled.protocols"),

	/**
	 * Defaults to SSL/TLS.
	 * 
	 * @see <a
	 *      href="http://docs.oracle.com/javase/8/docs/technotes/guides/management/agent.html#gdevf">Out-of-the-Box
	 *      Monitoring and Management Properties</a>
	 */
	JMX_REMOTE_SSL_ENABLED_CIPHER_SUITES("com.sun.management.jmxremote.ssl.enabled.cipher.suites"),

	/**
	 * Enabled by default.
	 * 
	 * @see <a
	 *      href="http://docs.oracle.com/javase/8/docs/technotes/guides/management/agent.html#gdevf">Out-of-the-Box
	 *      Monitoring and Management Properties</a>
	 */
	JMX_REMOTE_SSL_CLIENT_AUTH_ENABLED("com.sun.management.jmxremote.ssl.need.client.auth"),

	/**
	 * Enabled by default.
	 */
	JMX_REMOTE_AUTHENTICATION_ENABLED("com.sun.management.jmxremote.authenticate"),

	/**
	 * Defaults to $JRE_HOME/lib/management/jmxremote.password
	 * 
	 * @see <a
	 *      href="http://docs.oracle.com/javase/8/docs/technotes/guides/management/agent.html#gdevf">Out-of-the-Box
	 *      Monitoring and Management Properties</a>
	 */
	JMX_REMOTE_PASSWORD_FILE("com.sun.management.jmxremote.password.file"),

	/**
	 * Defaults to $JRE_HOME/lib/management/jmxremote.access
	 * 
	 * @see <a
	 *      href="http://docs.oracle.com/javase/8/docs/technotes/guides/management/agent.html#gdevf">Out-of-the-Box
	 *      Monitoring and Management Properties</a>
	 */
	JMX_REMOTE_ACCESS_FILE("com.sun.management.jmxremote.access.file")

	;

	private final String name;

	private StandardSystemProperty(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
