package org.testfun.jee.runner;

import org.testfun.jee.runner.inject.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DependencyInjector {

    private static final DependencyInjector INSTANCE = new DependencyInjector();

    public static DependencyInjector getInstance() {
        return INSTANCE;
    }

    private List<Injector> injectors = new LinkedList<>();

    private DependencyInjector() {
        MockRegistrar mockRegistrar = new MockRegistrar();
        injectors = Arrays.asList(
                mockRegistrar, // the MockRegistrar injector must be listed before the EJB injector to guarantee that mocks are registered before they are used.
                new EjbInjector().withMocking(mockRegistrar),
                new PersistenceContextInjector(),
                new ResourceInjector().withMocking(mockRegistrar)
        );
    }

    public <P> P injectDependencies(P pojo) {
        if (pojo == null) {
            return null;
        }

        // Loop over the class hierarchy of the target pojo
        for (Class clazz = pojo.getClass(); !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {

            // For each class, iterate over its field and inject a value if a proper injector was found for the field's annotation.
            // Note that the outer loop is over the injector so mock objects will be registered before any injection is attempted
            Field[] fields = clazz.getDeclaredFields();
            for (Injector injector : injectors) {
                for (Field field : fields) {
                    injector.inject(pojo, field);
                }
            }
        }

        return pojo;
    }

    public void reset() {
        for (Injector injector : injectors) {
            injector.reset();
        }
    }
}
