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

import static java.lang.System.currentTimeMillis;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.neverfear.jint.api.Jint.basic;

import java.io.PrintWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.neverfear.jint.api.Application;
import org.neverfear.jint.application.basic.BasicApplication;
import org.neverfear.jint.waitstrategy.ConsoleWaitStrategy;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Demonstrates console interactions.
 * 
 * @author doug@neverfear.org
 * 
 */
public class ConsoleWaitStrategyIT {

	private static final class AwaitStartTask
		implements Callable<Long> {

		private final CountDownLatch latch = new CountDownLatch(1);
		private final Application cat;

		/**
		 * @param cat
		 */
		private AwaitStartTask(final Application cat) {
			this.cat = cat;
		}

		@Override
		public Long call() throws Exception {
			this.latch.countDown();
			final long start = currentTimeMillis();
			this.cat.awaitStart();
			final long end = currentTimeMillis();
			return Long.valueOf(end - start);
		}

		public void awaitCall() throws InterruptedException {
			this.latch.await();
		}
	}

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Rule
	public TestRule timeout = new DisableOnDebug(Timeout.seconds(1));

	private final BasicApplication cat = basic("cat").waitStrategy(new ConsoleWaitStrategy("running"))
			.build();
	private PrintWriter stdin;

	private AwaitStartTask awaitStartTask;
	private ExecutorService executor;

	@Before
	public void before() throws Exception {
		this.cat.start();

		this.stdin = new PrintWriter(this.cat.console()
				.input(),
				true);

		this.awaitStartTask = new AwaitStartTask(this.cat);
		this.executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true)
				.build());
	}

	@After
	public void after() throws Exception {
		this.executor.shutdownNow();
		this.cat.stop();
		this.cat.awaitStop();
	}

	/**
	 * This is the simplest case
	 */
	@Test
	public void givenMatches_whenAwaitStart_expectDoneBeforeTestTimeout() throws Exception {
		this.stdin.println("cat is running now");
		this.cat.awaitStart();
	}

	/**
	 * This shows that it waits correctly until the match is found
	 */
	@Test
	public void givenMatchesAfterOneHalfSecond_whenAwaitStart_expectAwaitStartTookAtLeastOneHalfSecond()
			throws Exception {
		final Future<Long> future = this.executor.submit(this.awaitStartTask);
		this.awaitStartTask.awaitCall();
		this.stdin.println("not matching");
		Thread.sleep(500);
		this.stdin.println("cat is running now");
		final Long duration = future.get();
		assertThat(duration, greaterThanOrEqualTo(500L));
	}

	/**
	 * This shows were the match not to be found, this would linger.
	 */
	@Test
	public void givenNoMatchWithinOneHalfSecond_whenAwaitStart_expectTimeoutException()
			throws Exception {
		final Future<?> future = this.executor.submit(this.awaitStartTask);
		this.stdin.println("not matching");

		this.expectedException.expect(TimeoutException.class);
		future.get(500, TimeUnit.MILLISECONDS);
	}

}
