package org.testfun.jee.real;

import javax.ejb.Local;
import java.util.List;

@Local
public interface SomeDao {
    SomeEntity save(SomeEntity t);
    List<SomeEntity> getAll();
}
