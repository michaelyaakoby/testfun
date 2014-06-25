package org.testfun.jee;

import org.junit.Rule;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

public class JaxRsServerWithProviderTest {

    @Rule
    public JaxRsServer jaxRsServer = JaxRsServer.forResources(TestResource.class).providers(TestProvider.class);

    @Test
    public void getOk() throws Exception {
        JSONAssert.assertEquals(
                "{\"str\":\"kuki\",\"num\":2323}",
                jaxRsServer.jsonRequest("/rest/test/known").expectStatus(Response.Status.ACCEPTED).get(),
                JSONCompareMode.LENIENT
        );
    }

    @Provider
    public static class TestProvider implements ContainerResponseFilter {
        public TestProvider() {
            System.out.println();
        }

        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
            responseContext.setStatus(Response.Status.ACCEPTED.getStatusCode());
            responseContext.setEntity(new JaxRsTestObject("kuki", 2323));
        }
    }
}
