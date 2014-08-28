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
package org.neverfear.jint.application;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static org.neverfear.jint.util.CollectionUtil.varargToList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.neverfear.jint.api.Application;
import org.neverfear.jint.api.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Represents a group of applications that are started and stopped together.
 * Each group is a collection of applications that must be started or stopped
 * before the other. For example:
 * 
 * <pre>
 * Group group = Group.of(app1, app2)
 * 		.before(app3);
 * group.start();
 * </pre>
 * 
 * In this configuration app3 will not be started before app1 or app2, which may
 * themselves be started in any order relative to one another. Each group blocks
 * until all applications within the group enter the healthy start state. If any
 * fail, then all applications are cleanly stopped.
 * 
 * Conversely, if you were to now stop the group the stop order is reverse the
 * start order. In the above example this means that app3 is stopped first, then
 * app1 or app2. Again this blocks until all applications have successfully
 * stopped.
 * 
 * If any application fails to start, then all applications that were successful
 * are stopped in the reverse order. This allows you to use this group as an
 * all-or-nothing approach to starting applications.
 * 
 * @author doug@neverfear.org
 */
public final class Group {

	private static final Logger LOGGER = LoggerFactory.getLogger(Group.class);

	private final List<Collection<Application>> phases = Lists.newArrayList();
	private int size;

	private Group(final List<Application> applications) {
		this.size = applications.size();
		this.phases.add(applications);
	}

	public static Group of(final Application first, final Application... others) {
		return new Group(varargToList(first, others));
	}

	public static Group of(final Collection<? extends Application> applications) {
		return new Group(new ArrayList<>(applications));
	}

	public Group then(final Application first, final Application... others) {
		return then(varargToList(first, others));
	}

	public Group then(final Collection<? extends Application> applications) {
		this.size += applications.size();
		this.phases.add(new ArrayList<>(applications));
		return this;
	}

	/**
	 * Starts the application group in order. This operation is all-or-nothing.
	 * If any application fails to start, then any applications that were
	 * successfully will be cleanly stopped in reverse order.This method blocks
	 * until all applications have reached a started state.
	 * 
	 * @return
	 * @throws ApplicationException
	 * @throws InterruptedException
	 */
	public Group start() throws ApplicationException, InterruptedException {
		final List<Collection<Application>> startOrderPhases = calculateOrderOfStartPhases();

		final List<Application> successfulInReverse = newArrayListWithCapacity(this.size);
		Application current = null;

		try {

			for (final Collection<Application> phase : startOrderPhases) {

				for (final Application application : phase) {
					current = application;

					LOGGER.debug("Initiating start {}", application);
					application.start();
					successfulInReverse.add(0, application);
					LOGGER.debug("Start initiated {}", application);
				}

				for (final Application application : phase) {
					current = application;

					LOGGER.debug("Awaiting start of {}", application);
					application.awaitStart();
					LOGGER.debug("Started {}", application);
				}

			}

		} catch (final Exception e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("Failed to start " + current, e);
			}

			// Clean up all those applications that was started.
			stop(successfulInReverse);

			if (e instanceof InterruptedException) {
				throw e;
			} else {
				throw new ApplicationException(e);
			}
		}

		return this;
	}

	/**
	 * Stops the application group in reverse order. If any application fails to
	 * stop it will be skipped and the next application tried. This method
	 * blocks until all applications have reached a stopped state.
	 * 
	 * @return
	 * @throws ApplicationException
	 * @throws InterruptedException
	 */
	public Group stop() throws ApplicationException, InterruptedException {
		ApplicationException exception = null;

		final List<Collection<Application>> stopPhaseOrder = calculateOrderOfStopPhases();

		for (final Collection<Application> phase : stopPhaseOrder) {
			try {
				stop(phase);
			} catch (final Exception e) {
				if (exception == null) {
					exception = new ApplicationException(e);
				} else {
					exception.addSuppressed(e);
				}
			}
		}

		if (exception != null) {
			throw exception;
		}

		return this;
	}

	private void stop(final Collection<Application> stopOrder) throws ApplicationException, InterruptedException {
		ApplicationException exception = null;

		for (final Application application : stopOrder) {

			try {
				LOGGER.debug("Initiating stop {}", application);
				application.stop();
				LOGGER.debug("Stop initiated  {}", application);
			} catch (final Exception e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("Failed to stop " + application, e);
				}

				if (exception == null) {
					exception = new ApplicationException(e);
				} else {
					exception.addSuppressed(e);
				}
			}

		}

		awaitStop(stopOrder);

		if (exception != null) {
			throw exception;
		}
	}

	private void awaitStop(final Collection<Application> stopOrder) throws ApplicationException, InterruptedException {
		for (final Application application : stopOrder) {
			LOGGER.debug("Awaiting stop of {}", application);
			application.awaitStop();
			LOGGER.debug("Stopped {}", application);
		}
	}

	/**
	 * Awaits the natural stop of all applications in this group.
	 * 
	 * @throws ApplicationException
	 * @throws InterruptedException
	 */
	public void awaitStop() throws InterruptedException, ApplicationException {
		for (final Collection<Application> phase : calculateOrderOfStartPhases()) {
			for (final Application application : phase) {
				application.awaitStop();
			}
		}
	}

	private List<Collection<Application>> calculateOrderOfStartPhases() {
		return this.phases;
	}

	private List<Collection<Application>> calculateOrderOfStopPhases() {
		final List<Collection<Application>> stopPhaseOrder = newArrayList(calculateOrderOfStartPhases());
		Collections.reverse(stopPhaseOrder);
		return stopPhaseOrder;
	}

}
