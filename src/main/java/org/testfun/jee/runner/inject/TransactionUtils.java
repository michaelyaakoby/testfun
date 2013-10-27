package org.testfun.jee.runner.inject;

import org.junit.Assert;
import org.testfun.jee.runner.SingletonEntityManager;

import javax.ejb.ApplicationException;
import javax.persistence.EntityTransaction;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TransactionUtils {

    public static Object wrapEjbWithTransaction(Object impl) {
        Assert.assertNotNull("EJB Implementation is null", impl);
        Class<?>[] interfaces = impl.getClass().getInterfaces();

        return Proxy.newProxyInstance(TransactionUtils.class.getClassLoader(), interfaces, new TransactionalMethodWrapper(impl));
    }

    public static boolean beginTransaction() {
        EntityTransaction tx = getTransaction();
        if (!tx.isActive()) {
            tx.begin();
            return true;
        }

        return false;
    }

    public static void rollbackTransaction() {
        EntityTransaction tx = getTransaction();
        if (tx.isActive() && !tx.getRollbackOnly()) {
            // only flag the transaction for rollback - actual rollback will happen when the method starting the transaction is done
            tx.setRollbackOnly();
        }
    }

    public static void endTransaction(boolean newTransaction) {
        EntityTransaction tx = getTransaction();
        if (tx.isActive() && newTransaction) {
            if (tx.getRollbackOnly()) {
                // Rollback the transaction and close the connection if roll back was requested deeper in the stack and this is the method starting the transaction
                tx.rollback();
            }

            else {
                // Commit the transaction only if this is the method starting the transaction and rollback wasn't requested
                tx.commit();
            }
        }
    }

    private static EntityTransaction getTransaction() {
        return SingletonEntityManager.getInstance().getTransaction();
    }

    private static class TransactionalMethodWrapper implements InvocationHandler {

        private Object delegate;

        private TransactionalMethodWrapper(Object delegate) {
            this.delegate = delegate;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            boolean newTransaction = beginTransaction();

            try {
                return method.invoke(delegate, args);

            } catch (Throwable throwable) {

                if (throwable instanceof InvocationTargetException) {
                    InvocationTargetException invocationTargetException = (InvocationTargetException) throwable;

                    ApplicationException applicationException = invocationTargetException.getTargetException().getClass().getAnnotation(ApplicationException.class);

                    if (applicationException == null || applicationException.rollback()) {
                        rollbackTransaction();
                    }

                } else {
                    rollbackTransaction();
                }

                throw throwable.getCause();

            } finally {
                endTransaction(newTransaction);
            }
        }
    }

}
