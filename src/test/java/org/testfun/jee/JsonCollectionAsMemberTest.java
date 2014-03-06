package org.testfun.jee;


import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class JsonCollectionAsMemberTest {

    @Rule
    public JaxRsServer jaxRsServer = JaxRsServer.forResources(TestResource.class);

    @Test @Ignore
    public void emptyList() throws JSONException {
        JSONAssert.assertEquals(
                "{\"objectWithCollectionAsMember\":{\"collection\":[]}}",
                getCollection(0),
                JSONCompareMode.LENIENT
        );
    }

    @Test @Ignore
    public void listOfSizeOne() throws JSONException {
        JSONAssert.assertEquals(
                "{\"objectWithCollectionAsMember\":{\"collection\":[{\"str\":0,\"num\":0}]}}",
                getCollection(1),
                JSONCompareMode.LENIENT
        );
    }

    @Test
    public void listOfSizeBig() throws JSONException {
        JSONAssert.assertEquals(
                "{\"objectWithCollectionAsMember\":{\"collection\":[{\"str\":0,\"num\":0},{\"str\":1,\"num\":1},{\"str\":2,\"num\":2},{\"str\":3,\"num\":3},{\"str\":4,\"num\":4},{\"str\":5,\"num\":5},{\"str\":6,\"num\":6},{\"str\":7,\"num\":7},{\"str\":8,\"num\":8},{\"str\":9,\"num\":9}]}}",
                getCollection(10),
                JSONCompareMode.LENIENT
        );
    }

    private String getCollection(int value) {
        String length = jaxRsServer.jsonRequest("/rest/test/list").queryParam("length", value).get();
        System.out.println(length);
        return length;
    }
}


