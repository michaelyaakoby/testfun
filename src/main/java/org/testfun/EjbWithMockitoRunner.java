package org.testfun;

import org.testfun.runner.DependencyInjector;
import org.testfun.runner.inject.TransactionUtils;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.runners.util.FrameworkUsageValidator;

import java.lang.reflect.InvocationTargetException;

public class EjbWithMockitoRunner extends Runner implements Filterable {

    private BlockJUnit4ClassRunner runner;

    public EjbWithMockitoRunner(Class<?> klass) throws InvocationTargetException, InitializationError {
        runner = new BlockJUnit4ClassRunner(klass) {
            @Override
            protected Object createTest() throws Exception {
                Object test = super.createTest();

                // init annotated mocks before tests
                MockitoAnnotations.initMocks(test);

                // inject annotated EJBs before tests
                injectEjbs(test);

                // Rollback any existing transaction before starting a new one
                TransactionUtils.rollbackTransaction();
                TransactionUtils.endTransaction(true);

                // Start a new transaction
                TransactionUtils.beginTransaction();

                return test;
            }

        };

    }

    public void run(RunNotifier notifier) {
        // add listener that validates framework usage at the end of each test
        notifier.addListener(new FrameworkUsageValidator(notifier));

        runner.run(notifier);
    }

    public Description getDescription() {
        return runner.getDescription();
    }

    public void filter(Filter filter) throws NoTestsRemainException {
        runner.filter(filter);
    }

    private void injectEjbs(Object target) {
        DependencyInjector.getInstance().reset();
        DependencyInjector.getInstance().injectDependencies(target);
    }

}
