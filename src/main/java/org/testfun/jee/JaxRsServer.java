package org.testfun.jee;

import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.testfun.jee.runner.DependencyInjector;
import org.testfun.jee.runner.jaxrs.JaxRsException;
import org.testfun.jee.runner.jaxrs.RestRequest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.net.ServerSocket;

/**
 * A JUnit rule that launches a JAX-RS server (using RESTeasy and TJWS) running in the same JVM as the test itself.
 * Injection of EJBs and mocks into the JAX-RS resources requires running the test using the {@link EjbWithMockitoRunner} runner.
 */
public class JaxRsServer implements MethodRule {

    private int port = 0;

    private TJWSEmbeddedJaxrsServer jaxRsServer;

    private Class[] resourceClasses;

    private ExpectedClientResponseFailure expectedClientResponseFailure  = ExpectedClientResponseFailure.none();

    /**
     * Creates a JaxRsServer and deploys the specified resource classes.
     * @param resourceClasses one or more resource classes that should be deployed
     */
    public static JaxRsServer forResources(Class... resourceClasses) {
        return new JaxRsServer(resourceClasses);
    }

    private JaxRsServer(Class[] resourceClasses) {
        this.resourceClasses = resourceClasses;
    }

    /**
     * Optionally override the default selected port to bind to.
     * @param port TCP port to listen to
     * @return a new JaxRsServer
     */
    public JaxRsServer port(int port) {
        JaxRsServer newServer = new JaxRsServer(resourceClasses);
        newServer.port = port;
        return newServer;
    }

    /**
     * Gets the automatically-selected or manually-set TCP port used by the server.
     * @return selected TCP port
     */
    public int getPort() {
        return port;
    }

    /**
     * Constructs a new JSON REST request builder.
     * @param uri base request URI
     * @return REST request builder
     */
    public RestRequest jsonRequest(String uri) {
        return new RestRequest(uri, port).accept(MediaType.APPLICATION_JSON_TYPE);
    }

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        return new JaxRsServerStatement(expectedClientResponseFailure.apply(base, method, target));
    }

    /**
     * Set expectation for REST failure with a particular status code and a substring that should appear in the failure message.
     * @param expectedResponseStatus the HTTP status expected to be returned from the server
     * @param expectedMessageSubstring a substring of the expected message
     */
    public void expectFailureResponse(Response.Status expectedResponseStatus, String expectedMessageSubstring) {
        expectedClientResponseFailure.expectFailureResponse(expectedResponseStatus, expectedMessageSubstring);
    }

    public void startJaxRsServer() {
        jaxRsServer = new TJWSEmbeddedJaxrsServer();
        jaxRsServer.setPort(port);

        jaxRsServer.start();

        // If no port was set, than a free one was automatically selected - need to find it's number.
        if (port == 0) {
            Object server = getFromPrivateField(jaxRsServer, "server");
            Object acceptor = getFromPrivateField(server, "acceptor");
            ServerSocket socket = getFromPrivateField(acceptor, "socket");
            port = socket.getLocalPort();
        }

        for (Class aClass : resourceClasses) {
            Object resourceInstance;
            try {
                resourceInstance = aClass.newInstance();
            } catch (Exception e1) {
                throw new IllegalArgumentException(e1);
            }
            DependencyInjector.getInstance().injectDependencies(resourceInstance);
            jaxRsServer.getDeployment().getRegistry().addSingletonResource(resourceInstance);
        }
    }

    public void shutdownJaxRsServer() {
        jaxRsServer.stop();
        jaxRsServer.getDeployment().stop();
    }

    private class JaxRsServerStatement extends Statement {

        private final Statement next;

        private JaxRsServerStatement(Statement next) {
            this.next = next;
        }

        @Override
        public void evaluate() throws Throwable {
            startJaxRsServer();

            try {
                next.evaluate();

            } finally {
                shutdownJaxRsServer();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T, S> S getFromPrivateField(T obj, String fieldName) {
        // Locate the field in through all the super classes
        Field f = null;
        Class<?> objClass = obj.getClass();
        while(!objClass.equals(Object.class)) {
            try {
                f = objClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                // Ignore and try again with super
            }
            objClass = objClass.getSuperclass();
        }
        if (f == null) throw new JaxRsException("Could not find field '" + fieldName + "' in: " + obj);

        // Get the field's current accessibility
        boolean previousAccessState;
        try {
            previousAccessState = f.isAccessible();
        } catch (Exception e) {
            throw new JaxRsException("Could not get field's accessibility: " + fieldName, e);
        }

        // Change accessibility to true and get the field's value
        try {
            f.setAccessible(true);
            return (S)f.get(obj);
        } catch (Exception e) {
            throw new JaxRsException("Could not set field '" + fieldName + "'", e);
        } finally {

            // finally, restore field's accessibility
            f.setAccessible(previousAccessState);
        }
    }
}
