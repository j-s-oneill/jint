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
 * System properties of commonly available applications, that are not part of
 * the standard JRE.
 * 
 * @author doug@neverfear.org
 * 
 */
public enum CommonSystemProperty {

	LOGBACK_CONFIG_FILE("logback.configurationFile"),
	LOG4J_CONFIG_FILE("log4j.configuration"),
	JUL_CONFIG_FILE("java.util.logging.config.file");

	private final String name;

	private CommonSystemProperty(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
