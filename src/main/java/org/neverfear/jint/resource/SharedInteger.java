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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.neverfear.jint.util.FileUtil;

import com.google.common.annotations.VisibleForTesting;

/**
 * @author doug@neverfear.org
 * 
 */
public final class SharedInteger {

	private static final String LOCK_FILE = "lock";

	private final Clock clock;
	private final File parentDirectory;
	private final File lockFile;

	@VisibleForTesting
	SharedInteger(final Clock clock, final File parentDirectory) {
		FileUtil.ensureDirectoryExists(parentDirectory);
		this.parentDirectory = parentDirectory;
		this.clock = clock;
		this.lockFile = new File(parentDirectory,
				LOCK_FILE);
	}

	public SharedInteger(final File parentDirectory) {
		this(Clock.systemUTC(),
				parentDirectory);
	}

	private File valueFile(final int value) {
		return new File(this.parentDirectory,
				"" + value);
	}

	private boolean acquireIfAvailble(final File from) throws IOException {
		final ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(from,
				"rw")) {
			final FileChannel channel = randomAccessFile.getChannel();
			final Instant now = this.clock.instant();
			if (channel.read(buffer) != -1) {
				buffer.flip();
				final long epochMillis = buffer.getLong();
				if (now.compareTo(Instant.ofEpochMilli(epochMillis)) < 0) {
					// Not stale
					return false;
				}
				buffer.flip();
			}

			final Instant expiration = now.plus(1, ChronoUnit.DAYS);
			buffer.putLong(expiration.toEpochMilli());
			buffer.flip();
			while (buffer.hasRemaining()) {
				channel.write(buffer, 0);
			}
		}
		from.deleteOnExit();
		return true;
	}

	private boolean acquireIfAvailble(final int value) throws IOException {
		final File file = valueFile(value);
		return acquireIfAvailble(file);
	}

	public boolean tryAcquire(final int value) throws IOException {
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(this.lockFile,
				"rw")) {
			final FileChannel channel = randomAccessFile.getChannel();
			final FileLock lock = channel.lock();
			try {
				return acquireIfAvailble(value);
			} finally {
				lock.release();
			}
		}
	}
}
