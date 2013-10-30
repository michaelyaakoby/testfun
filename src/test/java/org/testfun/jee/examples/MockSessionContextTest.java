package org.testfun.jee.examples;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.testfun.jee.EjbWithMockitoRunner;

import javax.ejb.EJB;
import javax.ejb.SessionContext;
import java.security.Principal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(EjbWithMockitoRunner.class)
public class MockSessionContextTest {

    @Mock
    private SessionContext sessionContext;

    @EJB
    private UserEjb userEjb;

    @Test
    public void testSessionContextMock() {
        when(sessionContext.getCallerPrincipal()).thenReturn(new Principal() {
            @Override
            public String getName() {
                return "kuki";
            }
        });

        assertEquals("kuki", userEjb.getCurrentUser());
    }

}
