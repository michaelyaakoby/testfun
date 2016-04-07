package org.testfun.jee.examples;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testfun.jee.EjbWithMockitoRunner;
import org.testfun.jee.JaxRsServer;
import org.testfun.jee.runner.jaxrs.RestRequest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static org.junit.Assert.assertEquals;

@RunWith(EjbWithMockitoRunner.class)
public class FormParamsExampleTest {

    @Rule
    public JaxRsServer jaxRsServer = JaxRsServer.forResources(ExampleResource.class);

    @Test
    public void postWithForm() throws Exception {
        String response = new RestRequest("/example/form", jaxRsServer.getPort()).
                accept(MediaType.APPLICATION_FORM_URLENCODED_TYPE).
                body("p1=ABCD&p2=12345").
                expectStatus(Response.Status.OK).
                post();

        assertEquals("ABCD-12345", response);
    }
}
