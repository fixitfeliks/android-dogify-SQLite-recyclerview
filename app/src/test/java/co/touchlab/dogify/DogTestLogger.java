package co.touchlab.dogify;

import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class DogTestLogger {
    public static final TestRule getLogRule = new TestWatcher() {

        @Override
        protected void succeeded(final Description description) {
            System.out.println(String.format("Test Passed: %s", description));
        }

        @Override
        protected void failed(final Throwable e, final Description description) {
            System.out.println(String.format("Test Failed: %s\nError: %s", description, e));
        }

        @Override
        protected void starting(final Description description) {
            System.out.println(String.format("Test Starting: %s", description));
        }

        @Override
        protected void finished(final Description description) {
            System.out.println(String.format("Test Finished: %s\n", description));
        }
    };

    public static void logAssertEquals(String expected, String actual) {
        System.out.println(String.format("Expected: %s\nActual:   %s", expected, actual));
    }

    public static void logAssertNull(String actual) {
        System.out.println(String.format("Expected: NULL\nActual:   %s",  actual));
    }
}
