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
package org.neverfear.jint.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class CollectionUtil {

	private CollectionUtil() {
		throw new AssertionError();
	}

	public static <T> List<T> varargToList(final T first, final T[] others) {
		final List<T> list = new ArrayList<>(others.length + 1);
		list.add(first);
		list.addAll(Arrays.asList(others));
		return list;
	}

	public static <E> void replace(final Collection<E> into, final E[] values) {
		checkNotNull(into, "target cannot be null");
		checkNotNull(values, "source cannot be null");

		into.clear();
		for (final E value : values) {
			into.add(value);
		}
	}

	public static <E> void replace(final Collection<E> into, final Collection<E> values) {
		checkNotNull(into, "target cannot be null");
		checkNotNull(values, "source cannot be null");

		into.clear();
		into.addAll(values);
	}

	public static <E> void merge(final Collection<E> into, final E[] values) {
		checkNotNull(into, "target cannot be null");
		checkNotNull(values, "source cannot be null");

		for (final E value : values) {
			into.add(value);
		}
	}

	public static <E> void merge(final Collection<E> into, final Collection<E> values) {
		checkNotNull(into, "target cannot be null");
		checkNotNull(values, "source cannot be null");

		into.addAll(values);
	}

	public static <K, V> void merge(final Map<K, V> into, final K key, final V value) {
		checkNotNull(key, "key cannot be null");
		checkNotNull(value, "value cannot be null");

		into.put(key, value);
	}

	public static <K, V> void replace(final Map<K, V> into, final Map<K, V> values) {
		checkNotNull(into, "target cannot be null");
		checkNotNull(values, "source cannot be null");

		into.clear();
		into.putAll(values);
	}

	public static <K, V> void merge(final Map<K, V> into, final Map<K, V> values) {
		checkNotNull(into, "target cannot be null");
		checkNotNull(values, "source cannot be null");

		into.putAll(values);
	}
}
