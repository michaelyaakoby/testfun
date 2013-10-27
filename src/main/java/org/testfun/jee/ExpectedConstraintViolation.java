package org.testfun.jee;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Assert;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.mockito.ArgumentMatcher;

import javax.validation.ConstraintViolationException;

import static org.junit.matchers.JUnitMatchers.*;

public class ExpectedConstraintViolation implements MethodRule {

    /**
     * @return a Rule that expects no violation to be thrown
     * (identical to behavior without this Rule)
     */
    public static ExpectedConstraintViolation none() {
        return new ExpectedConstraintViolation();
    }

    private Matcher<Object> matcher;

    private ExpectedConstraintViolation() { }

    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        return new ExpectedExceptionStatement(base);
    }

    /**
     * Adds {@code matcher} to the list of requirements for any thrown exception.
     */
    // Should be able to remove this suppression in some brave new hamcrest world.
    @SuppressWarnings("unchecked")
    public void expect(Matcher<?> matcher) {
        if (this.matcher == null) {
            this.matcher = (Matcher<Object>) matcher;
        } else {
            this.matcher = both(this.matcher).and(matcher);
        }
    }

    /**
     * Adds to the list of requirements for any thrown exception that it
     * should <em>contain</em> string {@code substring}
     */
    public void expectViolation(String substring) {
        if (matcher == null) {
            expect(either(new CausedBy(org.hibernate.exception.ConstraintViolationException.class)).or(new CausedBy(ConstraintViolationException.class)));
        }

        expectMessage(containsString(substring));
    }

    /**
     * Adds {@code matcher} to the list of requirements for the message
     * returned from any thrown exception.
     */
    public void expectMessage(Matcher<String> matcher) {
        expect(matcherHasMessage(matcher));
    }

    private class ExpectedExceptionStatement extends Statement {
        private final Statement next;

        public ExpectedExceptionStatement(Statement base) {
            next = base;
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                next.evaluate();
            } catch (Throwable e) {
                if (matcher == null) {
                    throw e;
                }
                Assert.assertThat(e, matcher);
                return;
            }
            if (matcher != null) {
                throw new AssertionError("Expected test to throw "
                        + StringDescription.toString(matcher));
            }
        }
    }

    private Matcher<Throwable> matcherHasMessage(final Matcher<String> matcher) {
        return new TypeSafeMatcher<Throwable>() {
            public void describeTo(Description description) {
                description.appendText("violation with message ");
                description.appendDescriptionOf(matcher);
            }

            @Override
            public boolean matchesSafely(Throwable item) {
                return matcher.matches(item.getMessage());
            }
        };
    }

    class CausedBy extends ArgumentMatcher<Throwable> {

        private final Class<? extends Throwable> throwableClass;

        public CausedBy(Class<? extends Throwable> throwableClass) {
            this.throwableClass = throwableClass;
        }

        public boolean matches(Object argument) {
            return ExceptionUtils.indexOfThrowable((Throwable) argument, throwableClass) >= 0;
        }

        public void describeTo(Description description) {
            description.appendText("isCausedBy(" + throwableClass.getName() + ")");
        }

    }

}
