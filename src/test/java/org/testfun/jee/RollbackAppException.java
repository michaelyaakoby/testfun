package org.testfun.jee;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class RollbackAppException extends RuntimeException {
}
