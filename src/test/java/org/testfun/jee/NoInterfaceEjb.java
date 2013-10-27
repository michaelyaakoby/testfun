package org.testfun.jee;

import javax.ejb.Stateless;

@Stateless
public class NoInterfaceEjb {

    public String returnSomething() {
        return "something";
    }

}
