package com.framework.driver;

import org.openqa.selenium.WebDriver;

public class DriverManager {
    // ThreadLocal keeps a separate copy of the driver for every thread
    private static ThreadLocal<WebDriver> dr = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return dr.get();
    }

    public static void setDriver(WebDriver driver) {
        dr.set(driver);
    }

    public static void unload() {
        dr.remove();
    }
}
