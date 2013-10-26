package org.testfun.runner.inject;

import org.mockito.Mock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class MockRegistrar extends BaseInjector {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return Mock.class;
    }

    @Override
    public <T> void doInject(T target, Field field) {
        Object mock = InjectionUtils.readObjectFromField(target, field);
        if (mock != null) {
            registerByImplementedInterfaces(mock);

            // Also register the mock itself in case it's type doesn't implement any interface
            registerByClass(field.getType(), mock);
        }
    }

}
