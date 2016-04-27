package org.testfun.jee.examples;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testfun.jee.EjbWithMockitoRunner;
import org.testfun.jee.JaxRsServer;

import javax.ws.rs.core.Response;

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

    @Test
    public void getEmailFromSecurityContext() throws Exception {

        String response = jaxRsServer.
                jsonRequest("/example/user_from_security_context").
                basicAuth("kuki@puki.org", "kukipuki").
                expectStatus(Response.Status.OK).
                get();

        assertEquals("kuki@puki.org", response);
    }
}
