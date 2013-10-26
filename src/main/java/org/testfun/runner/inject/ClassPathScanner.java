package org.testfun.runner.inject;

import org.testfun.runner.EjbWithMockitoRunnerException;
import junit.framework.AssertionFailedError;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.Assert.fail;

public class ClassPathScanner {

    private static final Logger LOGGER = Logger.getLogger(ClassPathScanner.class);

    private Pattern pattern;

    public ClassPathScanner(Pattern pattern) {
        this.pattern = pattern;
    }

    public List<String> getResourcesInClassPath() {
        List<String> resourceNames = new LinkedList<>();

        String[] classPathRoots = System.getProperty("java.class.path", ".").split(";");
        for (String root : classPathRoots) {
            File rootFile = new File(root);
            if (rootFile.exists()) {
                if (rootFile.isDirectory()) {
                    findResourcesFromDirectory(resourceNames, rootFile.getAbsolutePath().length() + 1, rootFile, pattern);

                } else {
                    findResourcesFromJarFile(resourceNames, rootFile, pattern);
                }
            }
        }

        return resourceNames;
    }

    public void scan(Handler handler) {
        ClassLoader classLoader = getClass().getClassLoader();

        for (String resource : getResourcesInClassPath()) {

            try {
                String className = resource.replace('/', '.');
                className = className.substring(0, className.length() - 6); // Remove the ".class" suffix (6 characters) from the resource name

                Class<?> aClass = Class.forName(className, false, classLoader);
                handler.classFound(aClass);

            } catch (Throwable e) {
                if (e instanceof AssertionFailedError) {
                    fail(e.getMessage());//someone wanted this to fail...
                }
                LOGGER.trace("Failed determining class details for resource: " + resource, e);
            }
        }
    }

    public static interface Handler {
        public void classFound(Class<?> aClass);
    }

    private void findResourcesFromJarFile(List<String> resourceNames, File jarFile, Pattern pattern) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(jarFile);

            Enumeration<? extends ZipEntry> zipFileEntries = zipFile.entries();
            while (zipFileEntries.hasMoreElements()) {

                String fileName = zipFileEntries.nextElement().getName();

                if (pattern.matcher(fileName).matches()) {
                    resourceNames.add(fileName);
                }
            }

        } catch (Exception e) {
            throw new EjbWithMockitoRunnerException("Failed finding resources in JAR: " + jarFile, e);

        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    LOGGER.warn("Failed closing a resource", e);
                }
            }
        }
    }

    private void findResourcesFromDirectory(List<String> resourceNames, int rootLength, File directory, Pattern pattern) {
        File[] fileList = directory.listFiles();
        if (fileList != null) {
            for (File file : fileList) {

                if (file.isDirectory()) {
                    findResourcesFromDirectory(resourceNames, rootLength, file, pattern);

                } else {
                    String fileName = file.getAbsolutePath().substring(rootLength).replace('\\', '/');

                    if (pattern.matcher(fileName).matches()) {
                        resourceNames.add(fileName);
                    }
                }
            }
        }
    }

}
