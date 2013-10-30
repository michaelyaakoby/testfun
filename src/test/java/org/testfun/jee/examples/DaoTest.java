package org.testfun.jee.examples;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.testfun.jee.EjbWithMockitoRunner;
import org.testfun.jee.real.SomeDao;
import org.testfun.jee.real.SomeEntity;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(EjbWithMockitoRunner.class)
public class DaoTest {

    @EJB
    private SomeDao dao;

    @Test
    public void saveOne() {
        dao.save(new SomeEntity(0, "1. one", "s"));
        List<SomeEntity> entities = dao.getAll();
        assertEquals(1, entities.size());
    }

    @Test
    public void getMany() {
        dao.save(new SomeEntity(0, "1. one", "s"));
        dao.save(new SomeEntity(0, "2. two", "r"));
        List<SomeEntity> entities = dao.getAll();
        assertEquals(2, entities.size());
        assertThat(entities).
                onProperty("name").
                isEqualTo(Arrays.asList("1. one", "2. two"));//Note, this using org.fest.assertions.Assertions.assertThat
    }

}
