package org.testfun.jee;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@Stateless
public class EjbStateless implements EjbLocal {

    @EJB
    private MockEjbLocal mockEjb;

    @EJB
    private EjbSingleton ejbSingleton;

    @EJB
    private NoInterfaceEjb noInterfaceEjb;

    @Resource
    private SessionContext sessionContext;

    @Override
    public String returnFromNoInterfaceEjb() {
        return noInterfaceEjb.returnSomething();
    }

    @Override
    public String mockAnswer(String question) {
        return mockEjb.mockAnswer(question);
    }

    @Override
    public String realAnswer(String question) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        return engine.eval(question).toString();
    }

    @Override
    public void nonAppException() {
        throw new IllegalStateException("Should rollback non application exceptions");
    }

    @Override
    public void appException() {
        throw new AppException();
    }

    @Override
    public void appExceptionWithRollback() {
        throw new RollbackAppException();
    }

    @Override
    public String askSingleton() {
        return ejbSingleton.whoAmI();
    }

    @Override
    public String getCurrentUser() {
        return sessionContext.getCallerPrincipal().getName();
    }
}
