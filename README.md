Project TestFun - JEE JUnit Testing Is Fun
==========================================
Project TestFun's goal is to eliminate the common excuse for lack of good unit tests: "testing was too complicated".

TestFun-JEE is mixing existing libraries with our own goodies to deliver a robust but simple JEE junit experience when testing your DAOs (JPA), SLSBs, REST servers (JAX-RS) and more. 

### Features:
* **Injection of EJBs** (stateless sessions, singletons) and resources (JPA's entity manager, JDBC data-source, session-context) directly into JUnit test classes.
* **Injection of [Mockito](http://code.google.com/p/mockito/) mocks** into EJBs.
* **Transparent JDBC and JPA setup** - all you need is a persistence.xml file.
* **JAX-RS server testing** with EJB and mock injection.
* Simple transaction managment.

Usage
-----
### Getting started

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

Special thanks to...
--------------------
* [Project Lombok](http://projectlombok.org) for eliminating so much boiler plate code.
* [Mockito](http://code.google.com/p/mockito/) for its super cool mocking framework.
* [Junit](http://junit.org/)
