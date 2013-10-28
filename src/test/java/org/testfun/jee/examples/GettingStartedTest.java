package org.testfun.jee.examples;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.testfun.jee.EjbWithMockitoRunner;
import org.testfun.jee.real.SomeDao;
import org.testfun.jee.real.SomeEntity;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.Collections;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(EjbWithMockitoRunner.class)
public class GettingStartedTest {

    @EJB
    private Facade facade;

    @Mock
    private SomeDao dao;

    @Test
    public void notEntities() {
        when(dao.getAll()).thenReturn(Collections.<SomeEntity>emptyList());
        assertNull(facade.getFirstEntity());
    }

    @Test
    public void multipleEntities() {
        when(dao.getAll()).thenReturn(Arrays.asList(new SomeEntity("kuki", "puki")));
        assertEquals(new SomeEntity("kuki", "puki"), facade.getFirstEntity());
    }
}
