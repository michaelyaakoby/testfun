package org.testfun.jee.runner.inject;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

// Inject a data-source to @Inject annotated member variables with the appropriate class
public class InjectInjector extends BaseInjector {
    @Override
    public Class<? extends Annotation> getAnnotation() {
        return Inject.class;
    }

    @Override
    public <T> void doInject(T target, Field field) {
        final Object objectToInject = findInstanceByClass(field.getType());
        InjectionUtils.assignObjectToField(target, field, objectToInject);
    }
}
