package org.testfun.jee.real;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ProviderDaoImpl implements ProviderDao {

    @PersistenceContext(unitName = "noc")
    private EntityManager entityManager;

    @Override
    public Provider save(Provider t) {
        if (t.getId() == 0) {
            entityManager.persist(t);
        } else {
            entityManager.merge(t);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Provider> getAll() {
        Query query = entityManager.createQuery("FROM Provider AS be");
        return query.getResultList();
    }

}
