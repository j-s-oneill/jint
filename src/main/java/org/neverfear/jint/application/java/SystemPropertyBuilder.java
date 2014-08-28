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

import static com.google.common.collect.Maps.newHashMap;
import static org.neverfear.jint.resource.PortSupplier.validatePort;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

/**
 * Builder of common system properties.
 * 
 * @author doug@neverfear.org
 * 
 */
public class SystemPropertyBuilder {

	private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");
	private static final Joiner PATH_SEP_JOINER = Joiner.on(File.pathSeparatorChar);

	protected final Map<String, String> properties;

	public SystemPropertyBuilder() {
		this(Collections.<String, String> emptyMap());
	}

	public SystemPropertyBuilder(final SystemPropertyBuilder initial) {
		this(initial.properties);
	}

	public SystemPropertyBuilder(final Map<String, String> initial) {
		this.properties = newHashMap(initial);
	}

	private void put(final StandardSystemProperty property, final String value) {
		this.properties.put(property.getName(), value);
	}

	private void put(final CommonSystemProperty property, final String value) {
		this.properties.put(property.getName(), value);
	}

	public SystemPropertyBuilder userTimeZone(final TimeZone timeZone) {
		put(StandardSystemProperty.TIMEZONE, timeZone.getID());
		return this;
	}

	public SystemPropertyBuilder useUTC() {
		return userTimeZone(UTC_TIME_ZONE);
	}

	public SystemPropertyBuilder jmxRemotePort(final int port) {
		validatePort(port);

		/*
		 * We also enable jmx remote because in some environments it may have
		 * been disabled by default therefore we quietly re-enable it.
		 */
		this.put(StandardSystemProperty.JMX_REMOTE_ENABLED, Boolean.toString(true));
		this.put(StandardSystemProperty.JMX_REMOTE_PORT, Integer.toString(port));
		return this;
	}

	public SystemPropertyBuilder jmxDisableAuthenication() {
		this.put(StandardSystemProperty.JMX_REMOTE_AUTHENTICATION_ENABLED, Boolean.toString(false));
		return this;
	}

	public SystemPropertyBuilder jmxDisableSsl() {
		this.put(StandardSystemProperty.JMX_REMOTE_SSL_ENABLED, Boolean.toString(false));
		return this;
	}

	public SystemPropertyBuilder libraryPath(final String... paths) {
		put(StandardSystemProperty.JAVA_LIBRARY_PATH, PATH_SEP_JOINER
				.join(paths));
		return this;
	}

	public SystemPropertyBuilder logbackConfigFile(final String configFile) {
		put(CommonSystemProperty.LOGBACK_CONFIG_FILE, configFile);
		return this;
	}

	public SystemPropertyBuilder log4jConfigFile(final String configFile) {
		put(CommonSystemProperty.LOG4J_CONFIG_FILE, configFile);
		return this;
	}

	public SystemPropertyBuilder julConfigFile(final String configFile) {
		put(CommonSystemProperty.JUL_CONFIG_FILE, configFile);
		return this;
	}

	public SystemPropertyBuilder load(final Properties properties) {
		for (final Entry<Object, Object> entry : properties.entrySet()) {
			final String key = (String) entry.getKey();
			final String value = (String) entry.getValue();
			this.properties.put(key, value);
		}
		return this;
	}

	public SystemPropertyBuilder loadResource(final String resource) throws IOException {
		final Properties properties = new Properties();
		final InputStream is = SystemPropertyBuilder.class.getClassLoader()
				.getResourceAsStream(resource);
		if (is == null) {
			throw new FileNotFoundException(resource);
		}

		try {
			properties.load(is);
		} finally {
			is.close();
		}

		return load(properties);
	}

	public SystemPropertyBuilder loadFile(final File file) throws IOException {
		final Properties properties = new Properties();

		final InputStream is = new FileInputStream(file);
		try {
			properties.load(is);
		} finally {
			is.close();
		}

		return load(properties);
	}

	public final Map<String, String> build() {
		return Maps.newHashMap(this.properties);
	}
}
