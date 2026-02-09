package com.framework.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    static {
        properties = new Properties();
        // IMPROVEMENT: Load from Classpath (Works in JARs, Docker, and CI)
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                System.err.println("⚠️ Warning: config.properties not found on classpath.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        // 1. Priority: Command Line (-Dcloud_username=...)
        String value = System.getProperty(key);
        if (value != null && !value.isEmpty()) return value;

        // 2. Priority: Environment Variable (cloud_username -> CLOUD_USERNAME)
        // This allows YAML 'env:' blocks to work automatically!
        String envKey = key.toUpperCase().replace(".", "_");
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isEmpty()) return envValue;

        // 3. Priority: Config File (config.properties)
        return properties.getProperty(key);
    }
}
