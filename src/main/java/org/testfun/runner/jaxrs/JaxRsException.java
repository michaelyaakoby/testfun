package org.testfun.runner.jaxrs;

public class JaxRsException extends RuntimeException {
    public JaxRsException(String message, Throwable cause) {
        super(message, cause);
    }
}
