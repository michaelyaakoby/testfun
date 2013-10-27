package org.testfun.jee.runner.inject;

import org.testfun.jee.runner.EjbWithMockitoRunnerException;
import org.testfun.jee.runner.SingletonEntityManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;

// Inject an entityManager to @PersistenceContext annotated member variables
public class PersistenceContextInjector implements Injector {

    @Override
    public <T> void inject(T target, Field field) {
        if (field.isAnnotationPresent(PersistenceContext.class)) {

            // Make sure the field is of EntityManager interface
            Class<?> fieldClass = InjectionUtils.getFieldInterface(target, field);
            if (!EntityManager.class.equals(fieldClass)) {
                throw new EjbWithMockitoRunnerException(InjectionUtils.getFieldDescription(field, target) + " is annotated with PersistenceContext but isn't EntityManager");
            }

            // Assign the EntityManager to the field
            InjectionUtils.assignObjectToField(target, field, SingletonEntityManager.getInstance());
        }
    }

    @Override
    public void reset() { }

}
