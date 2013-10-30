package org.testfun.jee.examples;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testfun.jee.EjbWithMockitoRunner;
import org.testfun.jee.ExpectedConstraintViolation;
import org.testfun.jee.real.SomeDao;
import org.testfun.jee.real.SomeEntity;

import javax.ejb.EJB;

import static org.junit.Assert.assertEquals;

@RunWith(EjbWithMockitoRunner.class)
public class JpaValidationTest {

    @Rule
    public ExpectedConstraintViolation violationThrown = ExpectedConstraintViolation.none();

    @EJB
    private SomeDao someDao;

    @Test
    public void validName() {
        someDao.save(new SomeEntity(0, "Valid", null));
        assertEquals("Valid", someDao.getAll().get(0).getName());
    }

    @Test
    public void nameTooShort() {
        violationThrown.expectViolation("The name must be at least 4 character");
        someDao.save(new SomeEntity(0, "srt", null));
    }

    @Test
    public void nameTooLong() {
        violationThrown.expectViolation("The name must be less than 20 characters");
        someDao.save(new SomeEntity(0, "This name should be longer than 20 characters", null));
    }
}
