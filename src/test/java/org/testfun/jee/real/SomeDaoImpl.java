package org.testfun.jee.real;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class SomeDaoImpl implements SomeDao {

    @PersistenceContext(unitName = "TestFun")
    private EntityManager entityManager;

    @Override
    public SomeEntity save(SomeEntity t) {
        if (t.getId() == 0) {
            entityManager.persist(t);
        } else {
            entityManager.merge(t);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SomeEntity> getAll() {
        Query query = entityManager.createQuery("FROM SomeEntity AS be");
        return query.getResultList();
    }

}
