Project TestFun - JEE JUnit Testing Is Fun<br>(no server is needed!)
================================================================
Project TestFun's goal is to eliminate the common excuse for lack of good unit tests: "testing was too complicated".

TestFun-JEE is mixing existing libraries with our own goodies to deliver a robust but simple JEE junit experience when testing your DAOs (JPA), SLSBs, REST servers (JAX-RS) and more. 

### Features:
* **Injection of EJBs** (stateless sessions, singletons) and resources (JPA's entity manager, JDBC data-source, session-context) directly into JUnit test classes.
* **Injection of [Mockito](http://code.google.com/p/mockito/) mocks** into EJBs.
* **Transparent JDBC and JPA setup** - all you need is a persistence.xml file.
* **JAX-RS server testing** with EJB and mock injection.
* Simple transaction management.

Usage
-----
### Getting started
#### Adding TestFun-JEE to your Maven project
Using TestFun-JEE requires adding the following dependency to your POM:
```XML
<dependency>
    <groupId>org.testfun</groupId>
    <artifactId>jee</artifactId>
    <version>0.8</version>
    <scope>test</scope>
</dependency>
```
*TODO: The project isn't yet loaded into the central repository - please fork, build and install into your local .m2.*
#### Configuring JPA
JPA support is configured via the `src/test/resources/META-INF/persistence.xml`. 
This file defines the JDBC driver to be used as well as the "classes" folder containing the entities.
```xml
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd"
             version="1.0">

    <persistence-unit name="tests">

        <jar-file>../war/target/classes</jar-file>

        <properties>
            <property name="hibernate.connection.url" value="jdbc:mysql://localhost:3306?user=root&amp;password=******"/>

            <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL5InnoDBDialect"/>
            <property name="hibernate.ejb.naming_strategy" value="org.hibernate.cfg.ImprovedNamingStrategy"/>
            
            <property name="hibernate.hbm2ddl.auto" value="create"/>
        </properties>
    </persistence-unit>

</persistence>
```
This will configure a MySQL driver to be used by JPA or JDBC as well as the path where Hibernate will be looking for entity classes.
**Note** that this example uses the `hbm2ddl.auto=create` settings, however it is possible (and much faster) to create the schema during Maven build - TestFun-JEE will always rollback all changes to DB done during the tests (except for "implicit commits" caused by DDL commands).
#### Injecting EJB which uses a Mock object
In the following example we test a "facade" EJB which is using a "DAO" EJB which is accessing the DB. This test is mocking the DAO:
```java
@Data @AllArgsConstructor
@Table(catalog = "tmp", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Entity
public class SomeEntity {

    @Id
    @GeneratedValue
    private int id;

    @Length.List({
            @Length(min = 4, message = "The name must be at least 4 character"),
            @Length(max = 20, message = "The name must be less than 20 characters")
    })
    private String name;

    private String vcdApiAddress;

}
```
```java
@Local
public interface Facade {

    SomeEntity getFirstEntity();
}
```
```java
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
```
```java
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
```
**Note** that by mocking the `private SomeDao dao` member variable, any EJB asking for `SomeDao` to be injected will receive the same mock. 

### Testing EJBs which are using JPA and JDBC
As demonstrated above, all that is needed for testing an EJB (stateless/singleton/etc session-bean) which may be using JPA, JDBC and other EJBs is:

1. Replace the JUnit test runner by annotating the test class with `RunWith(EjbWithMockitoRunner.class)`.
2. Define a member variable using the interface of the EJB to be tested. Annotate this member with `@EJB`.
3. Mocking of dependencies of the EJB being tested is done by adding member variables to the tests whit the dependency's interface which are annotated with `@Mock`. Any reference in the test and in other EJBs to the mocked EJB will point to the mock object.

### Bean validation
To those lucky enough to use [Bean Validation](http://docs.oracle.com/javaee/6/tutorial/doc/gircz.html), TestFun-JEE allows you to easily assert these validations are working (after all, if such annotation is accidentally deleted, the compiler will not complain).

Simply set your failure message expectation before calling the EJB:
```java
@RunWith(EjbWithMockitoRunner.class)
public class JpaValidationTest {

    @Rule
    public ExpectedConstraintViolation violationThrown = ExpectedConstraintViolation.none();

    @EJB
    private SomeDao someDao;

    @Test
    public void validName() {
        someDao.save(new SomeEntity(0, "Valid", null));
        assertEquals("Valid", someDao.getAll().get(0).getName());
    }

    @Test
    public void nameTooShort() {
        violationThrown.expectViolation("The name must be at least 4 character");
        someDao.save(new SomeEntity(0, "srt", null));
    }

    @Test
    public void nameTooLong() {
        violationThrown.expectViolation("The name must be less than 20 characters");
        someDao.save(new SomeEntity(0, "This name should be longer than 20 characters", null));
    }
}
```

### Mocking SessionContext
If your EJBs are using the `SessionContext`, with TestFun-JEE mocking the context becomes very easy:
```java
@Local
public interface UserEjb {
    String getCurrentUser();
}
```
```java
@Stateless
public class UserEjbImpl implements UserEjb{

    @Resource
    private SessionContext sessionContext;

    @Override
    public String getCurrentUser() {
        return sessionContext.getCallerPrincipal().getName();
    }
}
```
```java
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
```

### Testing JAX-RS resources
#### Using Mockito mocks
#### Issuing JSON requests
#### Asserting JSON responses
#### Expecting failure responses

Special thanks to...
--------------------
* [Project Lombok](http://projectlombok.org) for eliminating so much boiler plate code.
* [Mockito](http://code.google.com/p/mockito/) for its super cool mocking framework.
* [Junit](http://junit.org/) for setting the goal.
* [RESTEasy](http://www.jboss.org/resteasy) for its sleek JAX-RS implementation and powerful testing infrastructure.
