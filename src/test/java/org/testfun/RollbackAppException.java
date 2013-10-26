package org.testfun;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class RollbackAppException extends RuntimeException {
}
