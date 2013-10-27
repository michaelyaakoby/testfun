package org.testfun.jee.runner;

import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SingletonDataSource {

    public static DataSource getDataSource() {
        return INSTANCE.dataSource;
    }

    private static final SingletonDataSource INSTANCE = new SingletonDataSource();

    private DataSource dataSource;

    private Connection delegateConnection;

    private SingletonDataSource() {
        try {
            Connection connection = DriverManager.getConnection(PersistenceXml.getInstnace().getConnectionUrl());
            connection.setAutoCommit(false);
            dataSource = (DataSource) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{DataSource.class}, new NotClosableDataSource(connection));
            Logger.getLogger(SingletonDataSource.class).info("Data source initialized successfully");

        } catch (SQLException e) {
            Logger.getLogger(SingletonDataSource.class).error("Data source initialization failed", e);
            throw new EjbWithMockitoRunnerException("Data source initialization failed", e);
        }
    }

    /**
     * A DataSource proxy that always return the same JDBC connection which doesn't close when "close" is called.
     * This is needed so JDBC calls will be using the same connection and transaction as JPA calls.
     * Note that the connection returned from the data-source is never closed as it is up to the entity manager to
     * close its connection
     */
    private class NotClosableDataSource implements InvocationHandler {

        private NotClosableDataSource(Connection connection) throws SQLException {
            delegateConnection = connection;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
            if ("getConnection".equals(method.getName())) {
                return getNotClosableConnection();
            }

            else if ("toString".equals(method.getName())) {
                return "NotClosableDataSource";
            }

            else {
                throw new IllegalArgumentException("Unsupported method: " + method.getName());
            }
        }

        private Object getNotClosableConnection() {
            return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Connection.class}, new NotClosableConnectionProxy(delegateConnection));
        }

    }

    /**
     * A JDBC Connection proxy that ignores calls to close() - used when the connection is retrieved from the entity manager.
     */
    private class NotClosableConnectionProxy implements InvocationHandler {

        private Connection delegate;

        private NotClosableConnectionProxy(Connection delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
            if ("close".equals(method.getName())) {
                return null;
            }

            else if ("toString".equals(method.getName())) {
                return "NotClosableConnectionProxy{" + delegate + "} - " + (delegate.isClosed() ? "closed" : "opened");
            }

            else {
                return method.invoke(delegate, args);
            }
        }
    }

}
