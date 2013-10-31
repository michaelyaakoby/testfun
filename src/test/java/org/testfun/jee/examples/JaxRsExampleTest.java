package org.testfun.jee.examples;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testfun.jee.EjbWithMockitoRunner;
import org.testfun.jee.JaxRsServer;

import javax.ws.rs.core.Response;

import static com.jayway.jsonassert.JsonAssert.with;
import static org.hamcrest.Matchers.contains;

@RunWith(EjbWithMockitoRunner.class)
public class JaxRsExampleTest {

    @Rule
    public JaxRsServer jaxRsServer = JaxRsServer.forResources(ExampleResource.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void get() throws Exception {
        JSONAssert.assertEquals(
                "{\"restData\":{\"key\":1,\"data\":\"Got 1\"}}",
                jaxRsServer.jsonRequest("/example/data/1").get(),
                JSONCompareMode.LENIENT
        );
    }

    @Test
    public void notFound() throws Exception {
        jaxRsServer.jsonRequest("/example/data/0").expectStatus(Response.Status.NOT_FOUND).get();
    }

    @Test
    public void getAll() throws Exception {
        with(jaxRsServer.jsonRequest("/example/data").queryParam("min", 2).queryParam("max", 6).get())
                .assertThat("$[*].restData.key", contains(2, 3, 4, 5));
    }

}
