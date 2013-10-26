package org.testfun.runner.inject;

import org.testfun.runner.SingletonDataSource;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

// Inject a data-source to @Resource annotated member variables with the appropriate class
public class ResourceInjector extends BaseInjector {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return Resource.class;
    }

    @Override
    public <T> void doInject(T target, Field field) {
        // Get field class and make sure the field is an interface
        Class<?> fieldClass = InjectionUtils.getFieldInterface(target, field);

        // Assign the DataSource to the field
        if (DataSource.class.equals(fieldClass)) {
            InjectionUtils.assignObjectToField(target, field, SingletonDataSource.getDataSource());
        }

        // Assign SessionContext to the field
        else if(SessionContext.class.equals(fieldClass)){
            InjectionUtils.assignObjectToField(target, field, findInstanceByClass(SessionContext.class));
        }
    }

}
