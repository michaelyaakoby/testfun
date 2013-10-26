package org.testfun;

import org.testfun.runner.PersistenceXml;
import org.testfun.runner.SingletonDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@RunWith(EjbWithMockitoRunner.class)
public class EjbWithMockitoRunnerTransactionTest {

    private static boolean rowAdded;

    @PersistenceContext
    private EntityManager entityManager;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void createTables() throws SQLException {
        dropTables();
        try (
                Connection connection = DriverManager.getConnection(PersistenceXml.getInstnace().getConnectionUrl());
                Statement statement = connection.createStatement()
        ) {
            statement.execute("CREATE DATABASE runner_test");
            statement.execute("CREATE TABLE runner_test.duplicates (id INTEGER NOT NULL AUTO_INCREMENT, name VARCHAR(200) NOT NULL, PRIMARY KEY (id), UNIQUE KEY uk_runner_test_name (name)) ENGINE=InnoDB");
        }

        rowAdded = false;
    }

    @AfterClass
    public static void dropTables() throws SQLException {
        // Must rollback the connection used by the entity manager in order to avoid locks due to inserts done before (recall transactions are never committed).
        SingletonDataSource.getDataSource().getConnection().rollback();

        try (
                Connection connection = DriverManager.getConnection(PersistenceXml.getInstnace().getConnectionUrl());
                Statement statement = connection.createStatement()
        ) {
            statement.execute("DROP DATABASE IF EXISTS runner_test");
        }
    }

    @Test
    public void test1() {
        firstAddRowNextVerify();
    }

    @Test
    public void test2() {
        firstAddRowNextVerify();
    }

    @Test
    public void dbThrowDuplicateKey() {
        thrown.expectMessage("Duplicate entry 'kuki' for key 'uk_runner_test_name'");

        entityManager.persist(new Duplicates("kuki"));
        entityManager.persist(new Duplicates("kuki"));
    }

    @Test
    public void preUpdate() {
        Duplicates entity = new Duplicates("kuki");
        entityManager.persist(entity);

        assertNull(entity.getDuplicateName());

        entity.setName("puki");
        entityManager.flush();

        assertEquals(entity.getName(), entity.getDuplicateName());
    }

    /**
     * This method is used for testing that a transaction is rolled back at the end of a test.
     * Because the rollback occurs after all @After methods are invoked, the test cannot be done using @After.
     * Also, there are no guarantees as for the order test methods are called.
     * This method solves the problem by performing "insert" on the first time it is called and "verify" on the second
     * time.
     */
    private void firstAddRowNextVerify() {
        if (rowAdded) {
            // If a row was already added by a previous run, verify it was rolled back.
            List rows = entityManager.createQuery("FROM Duplicates AS dups").getResultList();
            assertEquals(0, rows.size());
        }

        else {
            // If no row was added so far (its the first call to this method) than add a row.
            Duplicates row = new Duplicates("kuki");
            entityManager.persist(row);
            assertThat(row.getId(), not(0));

            assertSame(row, entityManager.find(Duplicates.class, row.getId()));

            rowAdded = true;
        }
    }

}
