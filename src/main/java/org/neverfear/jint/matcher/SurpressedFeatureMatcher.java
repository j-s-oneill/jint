package org.neverfear.jint.matcher;

import static java.util.Arrays.asList;

import java.util.Collection;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

public final class SurpressedFeatureMatcher
	extends FeatureMatcher<Throwable, Collection<Throwable>> {

	public SurpressedFeatureMatcher(final Matcher<? super Collection<Throwable>> subMatcher) {
		super(subMatcher,
				"Throwable with suppressed exceptions",
				"suppressed exceptions");
	}

	@Override
	protected Collection<Throwable> featureValueOf(final Throwable actual) {
		return asList(actual.getSuppressed());
	}

}