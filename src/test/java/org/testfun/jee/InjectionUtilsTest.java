package org.testfun.jee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.testfun.jee.runner.inject.InjectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class InjectionUtilsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getFieldInterface() throws NoSuchFieldException {
        Field field = ClassA.class.getDeclaredField("ser");
        Class<?> fieldInterface = InjectionUtils.getFieldInterface(new ClassA(), field);
        assertEquals(Serializable.class, fieldInterface);
    }

    @Test
    public void getFieldInterfaceFailed() throws NoSuchFieldException {
        thrown.expectMessage("field 'str' declared in class org.testfun.jee.InjectionUtilsTest$ClassA isn't an interface");
        Field field = ClassA.class.getDeclaredField("str");
        InjectionUtils.getFieldInterface(new ClassA(), field);
    }

    @Test
    public void assignObjectToField() throws NoSuchFieldException {
        ClassA a = new ClassA();

        InjectionUtils.assignObjectToField(a, ClassA.class.getDeclaredField("str"), "done!");
        assertEquals("done!", a.getStr());

        InjectionUtils.assignObjectToField(a, ClassA.class.getDeclaredField("in"), 142);
        assertEquals(142, a.getIn());
    }

    @Test
    public void assignObjectToFieldFailed() throws NoSuchFieldException {
        thrown.expectMessage("Failed injecting to field 'in' declared in class org.testfun.jee.InjectionUtilsTest$ClassA");
        InjectionUtils.assignObjectToField(new ClassA(), ClassA.class.getDeclaredField("in"), "not a number");
    }

    @Test
    public void readObjectFromField() throws NoSuchFieldException {
        ClassA classA = new ClassA(null, "setting", 56);
        int in = InjectionUtils.readObjectFromField(classA, ClassA.class.getDeclaredField("in"));
        assertEquals(56, in);
        assertEquals("setting", InjectionUtils.readObjectFromField(classA, ClassA.class.getDeclaredField("str")));
    }

    @Test
    public void readObjectFromFieldFailed() throws NoSuchFieldException {
        // Note that the expected message confuses the parent with child class - the reason is that in order trigger a
        // failure the test is trying to access a member of the child class while the object is of the parent class.
        thrown.expectMessage("Failed to read from field 'child' declared in class org.testfun.jee.InjectionUtilsTest$ClassB (superclass of class org.testfun.jee.InjectionUtilsTest$ClassA)");

        ClassA classA = new ClassA();
        InjectionUtils.readObjectFromField(classA, ClassB.class.getDeclaredField("child"));
    }

    @Test
    public void getFieldDescription() throws NoSuchFieldException {
        assertEquals(
                "field 'child' declared in class org.testfun.jee.InjectionUtilsTest$ClassB",
                InjectionUtils.getFieldDescription(ClassB.class.getDeclaredField("child"), new ClassB()));
        assertEquals(
                "field 'str' declared in class org.testfun.jee.InjectionUtilsTest$ClassA (superclass of class org.testfun.jee.InjectionUtilsTest$ClassB)",
                InjectionUtils.getFieldDescription(ClassA.class.getDeclaredField("str"), new ClassB()));
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    static class ClassA {
        private Serializable ser;
        private String str;
        private int in;
    }

    @Data @AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode(callSuper = true)
    static class ClassB extends ClassA {
        private String child;
    }



}
