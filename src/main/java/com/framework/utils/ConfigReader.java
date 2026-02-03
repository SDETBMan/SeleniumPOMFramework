package com.framework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    static {
        try {
            // Load the local file
            String path = "src/test/resources/config.properties";
            FileInputStream input = new FileInputStream(path);
            properties = new Properties();
            properties.load(input);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load config.properties file.");
        }
    }

    public static String getProperty(String key) {
        // 1. Check if a System Property exists (Command Line / CI override)
        String systemProp = System.getProperty(key);
        if (systemProp != null) {
            return systemProp;
        }

        // 2. Fallback to the config.properties file
        return properties.getProperty(key);
    }
}
