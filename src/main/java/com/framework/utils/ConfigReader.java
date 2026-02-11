package com.framework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigReader: Manages framework configuration with a strict priority hierarchy.
 * Priority: 1. System Properties (-Dkey=value)
 * 2. Environment Variables (KEY_NAME)
 * 3. Property File (config.properties)
 */
public class ConfigReader {
    private static Properties properties;

    static {
        properties = new Properties();

        // 1. Attempt to load from Classpath (Standard Maven/JAR execution)
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
                System.out.println("[INFO] Configuration loaded from Classpath.");
            } else {
                // 2. Fallback: Direct file system access (Local IDE execution)
                try (FileInputStream fileInput = new FileInputStream("src/test/resources/config.properties")) {
                    properties.load(fileInput);
                    System.out.println("[INFO] Configuration loaded from local file system.");
                } catch (Exception e) {
                    System.err.println("[FATAL] config.properties missing from both Classpath and File System.");
                }
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to initialize properties: " + e.getMessage());
        }
    }

    /**
     * Retrieves a property value based on the established priority hierarchy.
     */
    public static String getProperty(String key) {
        // Priority 1: Command Line / JVM Arguments (-Dbrowser=firefox)
        String value = System.getProperty(key);
        if (value != null && !value.isEmpty()) return value;

        // Priority 2: OS Environment Variables (BROWSER)
        String envKey = key.toUpperCase().replace(".", "_");
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isEmpty()) return envValue;

        // Priority 3: Values defined in config.properties
        return properties.getProperty(key);
    }
}
