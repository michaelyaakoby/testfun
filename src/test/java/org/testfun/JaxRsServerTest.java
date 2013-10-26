package org.testfun;

import org.testfun.JaxRsServer;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.ws.rs.core.Response;

public class JaxRsServerTest {

    @Rule
    public JaxRsServer jaxRsServer = JaxRsServer.forResources(TestResource.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void putWithHeaders() throws Exception {
        JSONAssert.assertEquals(
                "{\"jaxRsTestObject\":{\"str\":\"a string\",\"num\":1234}}",
                jaxRsServer.jsonRequest("/rest/test/put").header("str", "a string").header("num", 1234).put(),
                JSONCompareMode.LENIENT
        );
    }

    @Test
    public void postWithBody() throws Exception {
        JSONAssert.assertEquals(
                "{\"jaxRsTestObject\":{\"str\":\"a string\",\"num\":1234}}",
                jaxRsServer.jsonRequest("/rest/test/post").body(new JaxRsTestObject("a string", 1234)).post(),
                JSONCompareMode.LENIENT
        );
    }

    @Test
    public void postCreated() throws Exception {
        JSONAssert.assertEquals(
                "{\"jaxRsTestObject\":{\"str\":\"diet\",\"num\":4}}",
                jaxRsServer.jsonRequest("/rest/test/create").post(),
                JSONCompareMode.LENIENT
        );
    }

    @Test
    public void postCreatedWithLocation() throws Exception {
        jaxRsServer.jsonRequest("/rest/test/create").expectLocation("http://localhost/location").post();
        jaxRsServer.jsonRequest("/rest/test/create").expectLocation("/location").post();

        thrown.expectMessage("Expected location 'http://localhost/location/wrong' but got http://localhost/location");
        jaxRsServer.jsonRequest("/rest/test/create").expectLocation("http://localhost/location/wrong").post();
    }

    @Test
    public void getOk() throws Exception {
        JSONAssert.assertEquals(
                "{\"jaxRsTestObject\":{\"str\":\"Here it is\",\"num\":3456}}",
                jaxRsServer.jsonRequest("/rest/test/known").get(),
                JSONCompareMode.LENIENT
        );
    }

    @Test
    public void getWithParams() throws Exception {
        JSONAssert.assertEquals(
                "{\"jaxRsTestObject\":{\"str\":\"Here it is\",\"num\":111}}",
                jaxRsServer.jsonRequest("/rest/test/known").queryParam("num", 111).get(),
                JSONCompareMode.LENIENT
        );
    }

    @Test
    public void getNotFound() {
        jaxRsServer.expectFailureResponse(Response.Status.NOT_FOUND, "I can't find it");
        jaxRsServer.jsonRequest("/rest/test/unknown").get();
    }

    @Test
    public void expectStatusGet() {
        jaxRsServer.jsonRequest("/rest/test/known").expectStatus(Response.Status.OK).get();

        thrown.expectMessage("Expected response with status 200 (OK) but got response with status 404 (Not Found)");
        jaxRsServer.jsonRequest("/rest/test/unknown").expectStatus(Response.Status.OK).get();
    }

    @Test
    public void expectStatusPost() {
        jaxRsServer.jsonRequest("/rest/test/create").expectStatus(Response.Status.CREATED).post();

        thrown.expectMessage("Expected response with status 200 (OK) but got response with status 201 (Created)");
        jaxRsServer.jsonRequest("/rest/test/create").expectStatus(Response.Status.OK).post();
    }

    @Test
    public void expectWrongFailureStatus() {
        expectJaxRsServerException();

        jaxRsServer.expectFailureResponse(Response.Status.FORBIDDEN, "I can't find it");
        jaxRsServer.jsonRequest("/rest/test/unknown").get();
    }

    @Test
    public void expectWrongFailureMessage() {
        expectJaxRsServerException();

        jaxRsServer.expectFailureResponse(Response.Status.NOT_FOUND, "wrong message");
        jaxRsServer.jsonRequest("/rest/test/unknown").get();
    }

    @Test
    public void didNotFail() {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Expected test to fail");

        jaxRsServer.expectFailureResponse(Response.Status.NOT_FOUND, "I can't find it");
        jaxRsServer.jsonRequest("/rest/test/known").get();
    }

    @Test
    public void unexpectedFailure() {
        thrown.expect(ClientResponseFailure.class);
        thrown.expectMessage("Failed with status: 404");

        jaxRsServer.jsonRequest("/rest/test/unknown").get();
    }

    private void expectJaxRsServerException() {
        // "thrown" is evaluated after the jaxRsServer evaluation and is used for making sure jaxRsServer properly fails if
        // the unexpected failure is caught.
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Unexpected failure");
    }

}
