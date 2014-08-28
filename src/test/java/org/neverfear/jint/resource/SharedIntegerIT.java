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
package org.neverfear.jint.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author doug@neverfear.org
 * 
 */
public class SharedIntegerIT {

	private static final Instant NOW = Instant.ofEpochMilli(1262304000000L);
	private static final Instant NEW_EXPIRY = NOW.plus(1, ChronoUnit.DAYS);
	private static final Instant CLAIMED_EXPIRY = NOW.plus(1, ChronoUnit.HOURS);

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private final Clock clock = Clock.fixed(NOW, ZoneId.of("UTC"));
	private SharedInteger subject;
	private File claimed;
	private File empty;
	private File unclaimed;

	@Before
	public void before() throws Exception {
		final File tempDirectory = this.temporaryFolder.newFolder();
		this.subject = new SharedInteger(this.clock,
				tempDirectory);

		this.unclaimed = new File(tempDirectory,
				"5555");
		this.claimed = new File(tempDirectory,
				"12345");
		this.empty = new File(tempDirectory,
				"8888");
		this.empty.createNewFile();
		this.claimed.createNewFile();
		try (DataOutputStream out = new DataOutputStream(new FileOutputStream(this.claimed))) {
			out.writeLong(CLAIMED_EXPIRY.toEpochMilli());
		}
	}

	private Instant readExpirationTime(final File from) throws IOException {
		try (DataInputStream out = new DataInputStream(new FileInputStream(from))) {
			return Instant.ofEpochMilli(out.readLong());
		}
	}

	@Test
	public void givenEmptyFileOfMatchingName_whenTryAcquire_expectTrue_andFileContainsTomorrowAsNewExpiry()
			throws Exception {
		assertTrue(this.subject.tryAcquire(8888));
		assertEquals(NEW_EXPIRY, readExpirationTime(this.empty));
		assertFalse(this.subject.tryAcquire(8888));
	}

	@Test
	public void givenUnclaimed_whenTryAcquire_expectTrue_andFileContainsTomorrowAsNewExpiry() throws Exception {
		assertTrue(this.subject.tryAcquire(5555));
		assertEquals(NEW_EXPIRY, readExpirationTime(this.unclaimed));
		assertFalse(this.subject.tryAcquire(5555));
	}

	@Test
	public void givenClaimed_andNotStale_whenTryAcquire_expectFalse_andFileUntouched() throws Exception {
		assertFalse(this.subject.tryAcquire(12345));
		assertEquals(CLAIMED_EXPIRY, readExpirationTime(this.claimed));
	}

	@Test
	public void givenClaimed_andStale_whenTryAcquire_expectTrue_andFileContainsTomorrowAsNewExpiry() throws Exception {
		try (DataOutputStream out = new DataOutputStream(new FileOutputStream(this.claimed))) {
			final Instant now = this.clock.instant();
			final Instant expiration = now.minus(1, ChronoUnit.HOURS);
			out.writeLong(expiration.toEpochMilli());
		}

		assertTrue(this.subject.tryAcquire(12345));
		assertEquals(NEW_EXPIRY, readExpirationTime(this.claimed));
		assertFalse(this.subject.tryAcquire(12345));
	}

}
