package org.neverfear.jint.matcher;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

public final class RootCauseFeatureMatcher
	extends FeatureMatcher<Throwable, Throwable> {

	public RootCauseFeatureMatcher(final Matcher<? super Throwable> subMatcher) {
		super(subMatcher,
				"Throwable with root cause",
				"root cause");
	}

	@Override
	protected Throwable featureValueOf(final Throwable actual) {
		return rootCauseOf(actual);
	}

	public static Throwable rootCauseOf(final Throwable top) {
		final Throwable cause = top.getCause();
		if (cause == null) {
			return top;
		} else {
			return rootCauseOf(cause);
		}
	}

}