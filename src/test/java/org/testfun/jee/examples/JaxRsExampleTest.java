package org.testfun.jee.examples;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testfun.jee.EjbWithMockitoRunner;
import org.testfun.jee.JaxRsServer;
import org.testfun.jee.real.SomeDao;
import org.testfun.jee.real.SomeEntity;

import javax.ws.rs.core.Response;

import java.util.Arrays;

import static com.jayway.jsonassert.JsonAssert.with;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(EjbWithMockitoRunner.class)
public class JaxRsExampleTest {

    @Mock
    private SomeDao someDao;

    @Rule
    public JaxRsServer jaxRsServer = JaxRsServer.forResources(ExampleResource.class);

    @Test
    public void get() throws Exception {
        JSONAssert.assertEquals(
                "{\"restData\":{\"key\":1,\"data\":\"Got 1\"}}",
                jaxRsServer.jsonRequest("/example/data/1").get(),
                JSONCompareMode.LENIENT
        );
    }

    @Test
    public void notFound() {
        assertEquals(
                "Data with ID 0 wasn't found",
                jaxRsServer.jsonRequest("/example/data/0").expectStatus(Response.Status.NOT_FOUND).get()
        );
    }

    @Test
    public void getAll() {
        with(jaxRsServer.jsonRequest("/example/data").queryParam("min", 2).queryParam("max", 6).get())
                .assertThat("$[*].restData.key", contains(2, 3, 4, 5));
    }

    @Test
    public void create() {
        jaxRsServer
                .jsonRequest("/example/data")
                .body(new RestData(12, "data..."))
                .expectStatus(Response.Status.CREATED)
                .expectLocation("/example/data/12")
                .post();
    }

    @Test
    public void withMock() {
        when(someDao.getAll()).thenReturn(Arrays.<SomeEntity>asList(new SomeEntity(0, "n1", "a1"), new SomeEntity(0, "n2", "a2")));

        assertEquals("n1", jaxRsServer.jsonRequest("/example/use_ejb").header("index", 0).get());
        assertEquals("n2", jaxRsServer.jsonRequest("/example/use_ejb").header("index", 1).get());
    }
}
