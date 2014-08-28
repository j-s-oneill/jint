package org.neverfear.jint.matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;

public final class CausedByMatcher
	extends BaseMatcher<Throwable> {

	private final Throwable cause;

	public CausedByMatcher(final Throwable cause) {
		this.cause = cause;
	}

	@Override
	public boolean matches(final Object argument) {
		final Throwable throwable = (Throwable) argument;
		final Throwable actual = throwable.getCause();
		return CoreMatchers.equalTo(this.cause)
				.matches(actual);
	}

	@Override
	public void describeTo(final Description description) {
		description.appendValue(this.cause);
	}
}