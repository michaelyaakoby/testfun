package org.testfun.jee;

import org.junit.Rule;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class ConcurrentServersDifferentPortsTest {

    @Rule
    public JaxRsServer server1 = JaxRsServer.forResources(TestResource.class);

    @Rule
    public JaxRsServer server2 = JaxRsServer.forResources(TestResource.class);

    @Test
    public void makeSureTwoServersRunConcurrently() throws Exception {
        JSONAssert.assertEquals(
                "{\"str\":\"Here it is\",\"num\":3456}",
                server1.jsonRequest("/rest/test/known").get(),
                JSONCompareMode.LENIENT
        );
        JSONAssert.assertEquals(
                "{\"str\":\"Here it is\",\"num\":3456}",
                server2.jsonRequest("/rest/test/known").get(),
                JSONCompareMode.LENIENT
        );
    }

}
