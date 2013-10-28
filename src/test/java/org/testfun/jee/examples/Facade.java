package org.testfun.jee.examples;

import org.testfun.jee.real.SomeEntity;

import javax.ejb.Local;

@Local
public interface Facade {

    SomeEntity getFirstEntity();

}
