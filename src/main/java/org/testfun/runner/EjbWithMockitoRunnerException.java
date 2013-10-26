package org.testfun.runner;

public class EjbWithMockitoRunnerException extends RuntimeException {
    public EjbWithMockitoRunnerException(String message) {
        super(message);
    }

    public EjbWithMockitoRunnerException(String message, Throwable cause) {
        super(message, cause);
    }
}
