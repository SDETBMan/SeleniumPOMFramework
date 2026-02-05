package com.framework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigReader: The Central Configuration Hub.
 * * RESPONSIBILITY:
 * Loads configuration settings and handles the "Configuration Hierarchy":
 * 1. Command Line Arguments (Highest Priority) -> -Dbrowser=firefox
 * 2. Environment Variables (For Secrets) -> ${DB_PASSWORD}
 * 3. Config File (Defaults) -> config.properties
 */
public class ConfigReader {
    private static Properties properties;

    // Static block to load the file once when the class is accessed.
    static {
        try {
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

    /**
     * Retrieves a property value based on the priority hierarchy.
     * * @param key The key to look up (e.g., "browser", "db.password")
     * @return The resolved String value
     */
    public static String getProperty(String key) {
        // STEP 1: Check for Command Line Override (System Property)
        // WHY: This allows CI/CD (Jenkins/GitHub Actions) to override specific values
        // without changing code. Ex: 'mvn test -Dbrowser=firefox' overrides config.properties.
        String systemProp = System.getProperty(key);
        if (systemProp != null) {
            return systemProp;
        }

        // STEP 2: Fetch the value from the config.properties file
        String fileValue = properties.getProperty(key);

        // STEP 3: Check for "Secret Expansion" (Environment Variable Placeholder)
        // WHY: We never want to hardcode secrets. If the value looks like "${VAR_NAME}",
        // we assume it's a placeholder and look up the real secret in the System Environment.
        if (fileValue != null && fileValue.startsWith("${") && fileValue.endsWith("}")) {
            return getEnvironmentVariable(fileValue);
        }

        return fileValue;
    }

    /**
     * Helper method to parse the ${VAR_NAME} syntax and fetch the actual env var.
     */
    private static String getEnvironmentVariable(String placeholder) {
        // Remove the "${" prefix and "}" suffix to get the pure variable name.
        // Example: "${LOCAL_DB_PASSWORD}" -> "LOCAL_DB_PASSWORD"
        String varName = placeholder.substring(2, placeholder.length() - 1);

        String envValue = System.getenv(varName);

        // Safety Check: If the environment variable isn't set, throw an error so we fail fast.
        if (envValue == null) {
            throw new RuntimeException("CRITICAL ERROR: Environment Variable '" + varName + "' is not set in the system!");
        }
        return envValue;
    }
}
