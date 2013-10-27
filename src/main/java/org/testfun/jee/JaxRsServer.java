package org.testfun.jee;

import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.testfun.jee.runner.DependencyInjector;
import org.testfun.jee.runner.jaxrs.RestRequest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class JaxRsServer implements MethodRule {

    private static final int HTTP_PORT = 9095;

    private TJWSEmbeddedJaxrsServer jaxRsServer;

    private Class[] resourceClasses;

    private ExpectedClientResponseFailure expectedClientResponseFailure  = ExpectedClientResponseFailure.none();

    public static JaxRsServer forResources(Class... resourceClasses) {
        return new JaxRsServer(resourceClasses);
    }

    public JaxRsServer(Class[] resourceClasses) {
        this.resourceClasses = resourceClasses;
    }

    public RestRequest jsonRequest(String uri) {
        return new RestRequest(uri, HTTP_PORT).accept(MediaType.APPLICATION_JSON_TYPE);
    }

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        return new JaxRsServerStatement(expectedClientResponseFailure.apply(base, method, target));
    }

    /**
     * Set expectation for REST failure with a particular status code and a substring that should appear in the failure message.
     */
    public void expectFailureResponse(Response.Status expectedResponseStatus, String expectedMessageSubstring) {
        expectedClientResponseFailure.expectFailureResponse(expectedResponseStatus, expectedMessageSubstring);
    }

    private void startJaxRsServer() {
        jaxRsServer = new TJWSEmbeddedJaxrsServer();
        jaxRsServer.setPort(HTTP_PORT);

        jaxRsServer.start();

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

    private void shutdownJaxRsServer() {
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
}
