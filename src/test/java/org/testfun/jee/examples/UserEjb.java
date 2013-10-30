package org.testfun.jee.examples;

import javax.ejb.Local;

@Local
public interface UserEjb {
    String getCurrentUser();
}
