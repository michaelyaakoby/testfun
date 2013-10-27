package org.testfun.jee.runner.jaxrs;

public class JaxRsException extends RuntimeException {
    public JaxRsException(String message, Throwable cause) {
        super(message, cause);
    }
}
