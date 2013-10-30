package org.testfun.jee.examples;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

@Stateless
public class UserEjbImpl implements UserEjb{

    @Resource
    private SessionContext sessionContext;

    @Override
    public String getCurrentUser() {
        return sessionContext.getCallerPrincipal().getName();
    }
}
