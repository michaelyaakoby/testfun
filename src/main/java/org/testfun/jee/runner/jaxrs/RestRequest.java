package org.testfun.jee.runner.jaxrs;


import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import static org.fest.assertions.Assertions.assertThat;

public class RestRequest {

    private ClientRequest request;

    private MediaType contentType = MediaType.APPLICATION_XML_TYPE;

    private Response.Status expectedStatus;

    private String expectedLocationUri;

    public RestRequest(String uri, int port) {
        UriBuilder path = UriBuilder.fromUri("http://localhost").port(port).path(uri);
        request = new ClientRequest(path.build().toString());
    }

    public RestRequest accept(MediaType acceptMediaType) {
        request.accept(acceptMediaType);
        contentType = acceptMediaType;
        return this;
    }

    public RestRequest header(String key, Object value) {
        request.header(key, value);
        return this;
    }

    public RestRequest body(Object body) {
        request.body(contentType, body);
        return this;
    }

    public RestRequest queryParam(String param, Object value) {
        request.queryParameter(param, value);
        return this;
    }

    public RestRequest expectStatus(Response.Status expectedStatus) {
        this.expectedStatus = expectedStatus;
        return this;
    }

    public RestRequest expectLocation(String expectedLocationUri) {
        this.expectedLocationUri = expectedLocationUri;
        return this;
    }

    public String get() {
        return doHttpMethod("GET");
    }

    public String put() {
        return doHttpMethod("PUT");
    }

    public String post() {
        return doHttpMethod("POST");
    }

    public String delete() {
        return doHttpMethod("DELETE");
    }

    private String doHttpMethod(String method) {
        ClientResponse response;
        try {
            response = request.httpMethod(method);
        } catch (Exception e) {
            throw new JaxRsException(method + " failed", e);
        }

        assertExpectedStatus(response);
        assertLocation(response);
        return toString(response);
    }

    @SuppressWarnings("unchecked")
    private String toString(ClientResponse response) {
        Response.Status responseStatus = Response.Status.fromStatusCode(response.getStatus());

        if (responseStatus == Response.Status.NO_CONTENT) {
            return null;
        }
        if (responseStatus.getFamily() != Response.Status.Family.SUCCESSFUL && responseStatus != expectedStatus) {
            throw new ClientResponseFailure(response);
        } else {
            return (String) response.getEntity(String.class);
        }
    }

    private void assertExpectedStatus(ClientResponse response) {
        if (expectedStatus != null) {
            Response.Status responseStatus = Response.Status.fromStatusCode(response.getStatus());

            assertThat(responseStatus)
                    .as("Expected response with status " + expectedStatus.getStatusCode() + " (" + expectedStatus + ") but got response with status " + response.getStatus() + " (" + responseStatus + ")")
                    .isEqualTo(expectedStatus);
        }
    }

    private void assertLocation(ClientResponse response) {
        if (expectedLocationUri != null) {
            String actualLocation = response.getLocation().getHref();
            assertThat(actualLocation).
                    as("Expected location '" + expectedLocationUri + "' but got " + actualLocation).
                    contains(expectedLocationUri);
        }
    }

}
