package com.framework.utils;

import java.util.Random;

/**
 * StringUtils: Provides logic for string manipulation and random data generation.
 */
public class StringUtils {

    /**
     * Generates a random test email.
     * @return a string formatted as userXXX@test.com
     */
    public static String generateRandomEmail() {
        return "user" + new Random().nextInt(1000) + "@test.com";
    }

    /**
     * Reusable logic for string validation.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return email.contains("@") && email.contains(".");
    }
}
