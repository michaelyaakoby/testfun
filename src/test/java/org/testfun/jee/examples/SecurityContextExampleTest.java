package org.testfun.jee.examples;

import org.jboss.resteasy.plugins.server.servlet.ServletSecurityContext;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testfun.jee.EjbWithMockitoRunner;
import org.testfun.jee.JaxRsServer;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import static org.junit.Assert.assertEquals;

@RunWith(EjbWithMockitoRunner.class)
public class SecurityContextExampleTest {

    @Rule
    public JaxRsServer jaxRsServer = JaxRsServer.forResources(ExampleResource.class);

    @Test
    public void getUserFromSecurityContext() throws Exception {

        String response = jaxRsServer.
                jsonRequest("/example/user_from_security_context").
                basicAuth("kuki", "puki").
                expectStatus(Response.Status.OK).
                get();

        assertEquals("kuki", response);
    }
}
