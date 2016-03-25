package org.testfun.jee.runner.inject;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class ClassPathScannerTest {

    @Test
    public void testGetClasPathRoots() throws Exception {
        for(String root: new ClassPathScanner().getClasPathRoots()) {
            assertTrue(root + "doesn't exit", new File(root).exists());
        }
    }
}