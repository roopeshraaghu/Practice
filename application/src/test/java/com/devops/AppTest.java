package com.devops;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

public class AppTest {

    @Test
    public void testAppClassExists() {
        try {
            Class<?> appClass = Class.forName("com.devops.App");
            assertNotNull(appClass);
        } catch (ClassNotFoundException e) {
            fail("App class not found");
        }
    }

    @Test
    public void testMainMethodExists() {
        try {
            Class<?> appClass = Class.forName("com.devops.App");
            Method mainMethod = appClass.getMethod("main", String[].class);
            assertNotNull(mainMethod);
        } catch (Exception e) {
            fail("Main method not found: " + e.getMessage());
        }
    }

    @Test
    public void testHealthHandlerExists() {
        try {
            Class<?> healthHandlerClass = Class.forName("com.devops.App$HealthHandler");
            assertNotNull(healthHandlerClass);
        } catch (ClassNotFoundException e) {
            fail("HealthHandler class not found");
        }
    }

    @Test
    public void testRootHandlerExists() {
        try {
            Class<?> rootHandlerClass = Class.forName("com.devops.App$RootHandler");
            assertNotNull(rootHandlerClass);
        } catch (ClassNotFoundException e) {
            fail("RootHandler class not found");
        }
    }

    @Test
    public void testInfoHandlerExists() {
        try {
            Class<?> infoHandlerClass = Class.forName("com.devops.App$InfoHandler");
            assertNotNull(infoHandlerClass);
        } catch (ClassNotFoundException e) {
            fail("InfoHandler class not found");
        }
    }

    @Test
    public void testMetricsHandlerExists() {
        try {
            Class<?> metricsHandlerClass = Class.forName("com.devops.App$MetricsHandler");
            assertNotNull(metricsHandlerClass);
        } catch (ClassNotFoundException e) {
            fail("MetricsHandler class not found");
        }
    }

    @Test
    public void testAppProperties() {
        String javaVersion = System.getProperty("java.version");
        assertNotNull(javaVersion);
        assertTrue(javaVersion.startsWith("11") || javaVersion.startsWith("17"));
    }
}