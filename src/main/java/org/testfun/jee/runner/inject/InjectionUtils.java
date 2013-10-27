package org.testfun.jee.runner.inject;

import org.testfun.jee.runner.EjbWithMockitoRunnerException;

import java.lang.reflect.Field;

public class InjectionUtils {

    public static <T> Class<?> getFieldInterface(T target, Field field) {
        Class<?> fieldClass = field.getType();
        if (!fieldClass.isInterface()) {
            throw new EjbWithMockitoRunnerException(getFieldDescription(field, target) + " isn't an interface");
        }
        return fieldClass;
    }

    public static <T> void assignObjectToField(T target, Field field, Object value) {
        boolean fieldAccessible = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(target, value);
        } catch (Exception e) {
            throw new EjbWithMockitoRunnerException("Failed injecting to " + getFieldDescription(field, target), e);
        } finally {
            field.setAccessible(fieldAccessible);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T,O> O readObjectFromField(T target, Field field) {
        boolean fieldAccessible = field.isAccessible();
        field.setAccessible(true);
        try {
            return (O) field.get(target);
        } catch (Exception e) {
            throw new EjbWithMockitoRunnerException("Failed to read from " + getFieldDescription(field, target), e);
        } finally {
            field.setAccessible(fieldAccessible);
        }
    }

    public static <T> String getFieldDescription(Field field, T target) {
        Class<?> targetClass = target.getClass();
        Class<?> fieldDeclaringClass = field.getDeclaringClass();

        if (targetClass.equals(fieldDeclaringClass)) {
            return "field '" + field.getName() + "' declared in " + targetClass;
        } else {
            return "field '" + field.getName() + "' declared in " + fieldDeclaringClass + " (superclass of " + targetClass + ")";
        }
    }

}
