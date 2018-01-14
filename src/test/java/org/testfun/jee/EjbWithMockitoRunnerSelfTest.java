package org.testfun.jee;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.testfun.jee.real.SomeEntity;
import org.testfun.jee.real.SomeDao;
import org.testfun.jee.runner.SingletonEntityManager;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.script.ScriptException;
import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(EjbWithMockitoRunner.class)
public class EjbWithMockitoRunnerSelfTest {

    @EJB
    private SomeDao someDao;

    @EJB
    private EjbLocal ejb;

    @Mock
    private MockEjbLocal mock;

    @Mock
    private NoInterfaceEjb noInterfaceEjb;

    @Mock
    private SessionContext sessionContext;


    @PersistenceContext
    private EntityManager entityManager;

    @Resource
    private DataSource dataSource;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Rule
    public ExpectedConstraintViolation violationThrown = ExpectedConstraintViolation.none();

    @Test
    public void testEjbInjection() {
        // Test reading from DB using a DAO which uses JPA
        List<SomeEntity> all = someDao.getAll();
        assertTrue("No entities were found", all.isEmpty());

        // Test writing to DB using a DAO which uses JPA
        SomeEntity newSomeEntity = new SomeEntity(0, "name", "address");
        assertEquals("Entity ID", newSomeEntity.getId(), 0);
        someDao.save(newSomeEntity);
        assertTrue("Entity ID > 0", newSomeEntity.getId() > 0);

        // write to DB second time to make sure transactions are not committed nor rolled back
        someDao.save(new SomeEntity(0, "name2", "another address"));
        assertEquals("Added two new providers", all.size() + 2, someDao.getAll().size());

        // Make sure tests can fail (well... this test can't really guarantee this but it tries :D)
        String message = "Expected failure";
        exceptionRule.expect(AssertionError.class);
        exceptionRule.expectMessage(message);
        fail(message);
    }

    @Test
    public void testEjbInjectionIntoEjb() throws ScriptException {
        assertEquals("7", ejb.realAnswer("(5+2).toString()"));
        assertEquals("10", ejb.realAnswer("((7-4)*3+1).toString()"));
        assertEquals("me", ejb.askSingleton());
    }

    @Test
    public void testMockInjectionIntoEjb() {
        when(mock.mockAnswer("Whose motorcycle is this?")).thenReturn("It's a chopper baby.");
        when(mock.mockAnswer("Whose chopper is this?")).thenReturn("It's Zed's.");
        when(mock.mockAnswer("Who's Zed?")).thenReturn("Zed's dead baby, Zed's dead.");

        assertEquals("It's a chopper baby.", ejb.mockAnswer("Whose motorcycle is this?"));
        assertEquals("It's Zed's.", ejb.mockAnswer("Whose chopper is this?"));
        assertEquals("Zed's dead baby, Zed's dead.", ejb.mockAnswer("Who's Zed?"));
    }

    @Test
    public void testPersistenceInjection() throws SQLException {
        // Add a new provider using the entity manager directly (instead of an EJB)
        SomeEntity newSomeEntity = new SomeEntity(0, "name", "address");
        entityManager.persist(newSomeEntity);
        assertTrue("Entity ID > 0", newSomeEntity.getId() > 0);
        entityManager.flush();

        // Use a JDBC connection to read the newly added provider, make sure calling close doesn't close the connection...
        int newId;
        try(Connection connection = dataSource.getConnection()){
            try(Statement statement = connection.createStatement()) {
                try(ResultSet results = statement.executeQuery("SELECT id FROM SomeEntity WHERE name='name'")) {
                    results.next();
                    newId = results.getInt(1);
                }
            }
        }
        assertEquals("Entity ID", newSomeEntity.getId(), newId);
    }

    @Test
    public void testNonAppExceptionRollsBack() {
        SomeEntity newSomeEntity = new SomeEntity(0, "name", "address");
        someDao.save(newSomeEntity);

        try {
            ejb.nonAppException();
            fail("Should have thrown an exception");
        } catch (Exception e) {
            assertTrue("Transaction should have been rolled back", SingletonEntityManager.getInstance().getTransaction().getRollbackOnly());
        }
    }

    @Test
    public void testAppException() {
        SomeEntity newSomeEntity = new SomeEntity(0, "name", "address");
        someDao.save(newSomeEntity);

        try {
            ejb.appException();
            fail("Should have thrown an exception");
        } catch (Exception e) {
            assertFalse("Transaction should not be rolled back", SingletonEntityManager.getInstance().getTransaction().getRollbackOnly());
        }
    }

    @Test
    public void testRollingBackAppException() {
        SomeEntity newSomeEntity = new SomeEntity(0, "name", "address");
        someDao.save(newSomeEntity);

        try {
            ejb.appExceptionWithRollback();
            fail("Should have thrown an exception");
        } catch (Exception e) {
            assertTrue("Transaction should have been rolled back", SingletonEntityManager.getInstance().getTransaction().getRollbackOnly());
        }
    }

    @Test
    public void testJavaxValidationExpectations() {
        violationThrown.expectViolation("javax.validation.ConstraintViolationException: Untested code violates the boys scout's rule!");

        Set<ConstraintViolation<?>> violations = Collections.emptySet();
        throw new ConstraintViolationException("javax.validation.ConstraintViolationException: Untested code violates the boys scout's rule!", violations);
    }

    @Test
    public void testHibernateValidationExpectations() {
        violationThrown.expectViolation("org.hibernate.exception.ConstraintViolationException: Untested code violates the boys scout's rule!");

        throw new org.hibernate.exception.ConstraintViolationException("org.hibernate.exception.ConstraintViolationException: Untested code violates the boys scout's rule!", null, null);
    }

    @Test
    public void testMockOfNoInterfaceEjb() {
        when(noInterfaceEjb.returnSomething()).thenReturn("nothing");
        assertEquals("nothing", ejb.returnFromNoInterfaceEjb());
    }

    @Test
    public void testSessionContextMock() {
        when(sessionContext.getCallerPrincipal()).thenReturn(new Principal() {
            @Override
            public String getName() {
                return "kuki";
            }
        });

        assertEquals("kuki", ejb.getCurrentUser());
    }
}
