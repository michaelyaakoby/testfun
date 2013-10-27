package org.testfun.jee.runner;

import org.hibernate.cfg.AvailableSettings;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class SingletonEntityManager {

    public static EntityManager getInstance() {
        return INSTANCE.getEntityManager();
    }

    private static final SingletonEntityManager INSTANCE = new SingletonEntityManager();

    private EntityManager entityManager;

    private SingletonEntityManager() {
    }

    private synchronized EntityManager getEntityManager() {
        if (entityManager == null) {
            Map<String, DataSource> config = new HashMap<>();
            config.put(AvailableSettings.DATASOURCE, SingletonDataSource.getDataSource());

            EntityManagerFactory emf = Persistence.createEntityManagerFactory(PersistenceXml.getInstnace().getPersistenceUnitName(), config);
            entityManager = emf.createEntityManager();
        }

        return entityManager;
    }
}
