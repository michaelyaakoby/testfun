package org.testfun.real;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ProviderDao {
    Provider save(Provider t);
    List getAll();
}
