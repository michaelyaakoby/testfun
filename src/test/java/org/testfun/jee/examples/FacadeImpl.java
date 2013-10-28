package org.testfun.jee.examples;

import org.testfun.jee.real.SomeDao;
import org.testfun.jee.real.SomeEntity;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class FacadeImpl implements Facade {

    @EJB
    private SomeDao dao;

    @Override
    public SomeEntity getFirstEntity() {
        List<SomeEntity> entities = dao.getAll();
        return entities.size() > 0 ? entities.get(0) : null;
    }
}
