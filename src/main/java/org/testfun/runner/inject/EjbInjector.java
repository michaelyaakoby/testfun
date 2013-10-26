package org.testfun.runner.inject;

import org.testfun.runner.DependencyInjector;
import org.testfun.runner.EjbWithMockitoRunnerException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

// Lookup and inject to @EJB annotated member variables
public class EjbInjector extends BaseInjector {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return EJB.class;
    }

    @Override
    public <T> void doInject(T target, Field field) {
        Object ejb;

        // If the field's class is a singleton or a stateless-session, than it isn't expected to implement any interface
        // so the class itself is registered
        if (field.getType().getAnnotation(Singleton.class) != null || field.getType().getAnnotation(Stateless.class) != null) {
            Class<?> fieldClass = field.getType();

            // Search for an instance of this interface or a mock

            ejb = findInstanceByClass(fieldClass);

            // If none was found, instantiate a new one and register the it in the various caches
            if (ejb == null) {
                ejb = instantiateSingletonAndCache(fieldClass);
            }
        }

        else {
            // Get class of the field  and make sure it is an interface
            Class<?> fieldClass = InjectionUtils.getFieldInterface(target, field);

            // Search for an instance of this interface or a mock

            ejb = findInstanceByClass(fieldClass);

            // If none was found, instantiate a new one and register the it in the various caches
            if (ejb == null) {
                ejb = instantiateAndCache(fieldClass);
            }
        }

        // Assign the EJB to the field
        InjectionUtils.assignObjectToField(target, field, ejb);
    }

    private Object instantiateAndCache(Class<?> fieldClass) {
        // Try to instantiate an implementation of the field's class
        Object transactionLessEjb = EjbInstanceFactory.getInstance().newInstance(fieldClass);

        // Wrap the EJB with a transaction.
        Object ejb = TransactionUtils.wrapEjbWithTransaction(transactionLessEjb);

        registerByImplementedInterfaces(ejb);

        // Finally, inject dependencies into the new EJB
        DependencyInjector.getInstance().injectDependencies(transactionLessEjb);

        return ejb;
    }

    private Object instantiateSingletonAndCache(Class<?> fieldClass) {
        // Try to instantiate an implementation of the singleton
        Object singleton = EjbInstanceFactory.getInstance().newInstance(fieldClass);

        registerByClass(fieldClass, singleton);

        // Inject dependencies into the new EJB
        DependencyInjector.getInstance().injectDependencies(singleton);

        // Invoke any method annotated with @PostConstruct
        Method[] declaredMethods = fieldClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                try {
                    method.invoke(singleton);
                } catch (Exception e) {
                    throw new EjbWithMockitoRunnerException("Failed invoking '" + method.getName() + "'", e);
                }
            }
        }

        return singleton;
    }

}
