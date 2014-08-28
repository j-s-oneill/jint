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

import java.io.IOException;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.neverfear.jint.api.Application;

/**
 * This implementation waits until a named JMX bean is registered.
 * 
 * @author doug@neverfear.org
 * 
 */
public final class JmxBeanRegistrationHealthCheck
	implements HealthCheck {

	private final ObjectName beanName;
	private final String serviceURL;

	public JmxBeanRegistrationHealthCheck(final ObjectName beanName, final int port) {
		this(beanName,
				"localhost",
				port);
	}

	public JmxBeanRegistrationHealthCheck(final ObjectName beanName, final String hostname, final int port) {
		super();
		this.beanName = beanName;
		this.serviceURL = "service:jmx:rmi:///jndi/rmi://" + hostname + ":" + port + "/jmxrmi";
	}

	@Override
	public boolean isHealthy(final Application application) {
		try {
			final JMXConnector jmxc = JMXConnectorFactory.connect(new JMXServiceURL(this.serviceURL));
			final MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			mbsc.getMBeanInfo(this.beanName);
			return true;
		} catch (final IOException | InstanceNotFoundException | IntrospectionException | ReflectionException e) {
			return false;
		}
	}
}
