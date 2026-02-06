package com.framework.driver;

import org.openqa.selenium.WebDriver;

public class DriverManager {
    // 1. ThreadLocal ISOLATES the driver for each thread.
    // This ensures there is only ONE container for threads to access.
    // Without 'static', every call might try to access a different instance.
    private static final ThreadLocal<WebDriver> dr = new ThreadLocal<>();

    // 2. Private constructor to prevent instantiation
    private DriverManager() {}

    // 3. GET Method
    public static WebDriver getDriver() {
        // DEBUG LOG
        // System.out.println("DEBUG: Getting Driver... Value is: " + dr.get());
        return dr.get();
    }

    // 4. SET Method
    public static void setDriver(WebDriver driverRef) {
        // DEBUG LOG
        System.out.println("DEBUG: Setting Driver in Manager: " + driverRef);
        dr.set(driverRef);
    }

    // 5. UNLOAD Method
    public static void unload() {
        System.out.println("DEBUG: Removing Driver from Manager.");
        dr.remove();
    }
}
