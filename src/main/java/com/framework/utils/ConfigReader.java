package com.framework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    static {
        properties = new Properties();

        // 1. Try loading from Classpath (Standard Maven way)
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
                System.out.println("✅ Config Loaded from Classpath");
            } else {
                // 2. Fallback: Try loading directly from file system (Local IDE/Debug way)
                System.out.println("Config not found in Classpath. Trying local file system...");
                try (FileInputStream fileInput = new FileInputStream("src/test/resources/config.properties")) {
                    properties.load(fileInput);
                    System.out.println("Config Loaded from Local File System");
                } catch (Exception e) {
                    System.err.println("FATAL: config.properties not found in Classpath OR File System!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        // 1. Priority: Command Line (-Dusername=...)
        String value = System.getProperty(key);
        if (value != null && !value.isEmpty()) return value;

        // 2. Priority: Environment Variable (username -> USERNAME)
        String envKey = key.toUpperCase().replace(".", "_");
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isEmpty()) return envValue;

        // 3. Priority: Config File (config.properties)
        return properties.getProperty(key);
    }
}
