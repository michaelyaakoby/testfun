package org.testfun.jee.runner.inject;

import org.testfun.jee.runner.EjbWithMockitoRunnerException;

import javax.ejb.Singleton;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EjbInstanceFactory {

    private static final EjbInstanceFactory INSTANCE = new EjbInstanceFactory();

    public static EjbInstanceFactory getInstance() {
        return INSTANCE;
    }

    public static final Pattern CLASS_NAME_PATTERN = Pattern.compile("org/testfun/[^$]+\\.class"); //TODO this needs to be externalized to a config file

    private Map<Class, Class> ejbClassByImplementedInterface;

    private EjbInstanceFactory() { }

    public Object newInstance(Class<?> implementedInterface) {
        Class aClass = getEjbClassByImplementedInterface().get(implementedInterface);
        if (aClass == null) {
            throw new EjbWithMockitoRunnerException("Implementation for interface not found: " + implementedInterface);
        }

        try {
            return aClass.newInstance();
        } catch (Exception e) {
            throw new EjbWithMockitoRunnerException("Failed to instantiate interface: " + implementedInterface.getName() , e);
        }

    }

    private Map<Class, Class> getEjbClassByImplementedInterface() {
        if (ejbClassByImplementedInterface == null) {
            ejbClassByImplementedInterface = new HashMap<>();

            ClassPathScanner classPathScanner = new ClassPathScanner(CLASS_NAME_PATTERN);
            classPathScanner.scan(new ClassPathScanner.Handler() {
                @Override
                public void classFound(Class<?> aClass) {
                    if (!aClass.isInterface()) {

                        // If the class is annotated with @Stateless, register all the implemented interfaces
                        if (aClass.getAnnotation(Stateless.class) != null) {
                            for (Class<?> implementedInterface : aClass.getInterfaces()) {
                                ejbClassByImplementedInterface.put(implementedInterface, aClass);
                            }

                            // Also, register the stateless as if it implements itself so the factory will work for SLSB that doesn't implement any interface.
                            ejbClassByImplementedInterface.put(aClass, aClass);
                        }

                        // If the class is annotated with @Singleton then register the singleton as if it implements "itself" -
                        // this will allow users to get the "implementing class" of the singleton
                        else if (aClass.getAnnotation(Singleton.class) != null) {
                            for (Class<?> implementedInterface : aClass.getInterfaces()) {
                                ejbClassByImplementedInterface.put(implementedInterface, aClass);
                            }
                            ejbClassByImplementedInterface.put(aClass, aClass);
                        }
                    }
                }
            });
        }

        return ejbClassByImplementedInterface;
    }
}
