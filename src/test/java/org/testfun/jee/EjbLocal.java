package org.testfun.jee;

import javax.ejb.Local;
import javax.script.ScriptException;

@Local
public interface EjbLocal {
    String mockAnswer(String question);
    String realAnswer(String question) throws ScriptException;
    void nonAppException();
    void appException();
    void appExceptionWithRollback();
    String askSingleton();
    String returnFromNoInterfaceEjb();
    String getCurrentUser();
}
