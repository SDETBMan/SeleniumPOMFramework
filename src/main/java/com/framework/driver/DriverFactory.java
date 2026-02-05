package com.framework.driver;


import com.framework.utils.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * DriverFactory: Universal logic for Web, Android, and iOS.
 */
public class DriverFactory {

    public static WebDriver createInstance(String browser, String headless) throws MalformedURLException {
        WebDriver driver = null;

        String mode = ConfigReader.getProperty("execution_mode");
        String gridUrl = ConfigReader.getProperty("grid_url");
        String cloudUrl = ConfigReader.getProperty("cloud_grid_url");
        String appiumUrl = "http://127.0.0.1:4723";

        // --- DEBUG PRINT (This is what we need to see!) ---
        System.out.println("=========================================");
        System.out.println("DEBUG: DriverFactory initialized.");
        System.out.println("DEBUG: Execution Mode from Config: [" + mode + "]");
        System.out.println("DEBUG: Browser/Platform Requested: [" + browser + "]");
        System.out.println("=========================================");
        // --------------------------------------------------

        boolean isHeadless = Boolean.parseBoolean(headless);

        // ==========================================================
        // 1. SETUP OPTIONS (Configuration objects)
        // ==========================================================

        // WEB OPTIONS
        ChromeOptions chromeOptions = new ChromeOptions();
        if (isHeadless) chromeOptions.addArguments("--headless");

        FirefoxOptions firefoxOptions = new FirefoxOptions();
        if (isHeadless) firefoxOptions.addArguments("-headless");

        EdgeOptions edgeOptions = new EdgeOptions();
        if (isHeadless) edgeOptions.addArguments("--headless");

        // ANDROID OPTIONS
        UiAutomator2Options androidOptions = new UiAutomator2Options();
        if (browser.equalsIgnoreCase("android")) {
            androidOptions.setDeviceName(ConfigReader.getProperty("android_device_name"));
            androidOptions.setApp(ConfigReader.getProperty("android_app_path"));
            androidOptions.setAutomationName("UiAutomator2");

            // 1. Wait for ANY activity to start (Fixes "SplashActivity never started")
            androidOptions.setCapability("appWaitActivity", "*");
            // 2. Give the emulator 30 seconds to wake up and launch the app
            androidOptions.setCapability("appWaitDuration", 30000);
            // ---------------------------
        }

        // iOS OPTIONS
        XCUITestOptions iosOptions = new XCUITestOptions();
        if (browser.equalsIgnoreCase("ios")) {
            iosOptions.setDeviceName(ConfigReader.getProperty("ios_device_name"));
            iosOptions.setPlatformVersion(ConfigReader.getProperty("ios_version"));
            iosOptions.setApp(ConfigReader.getProperty("ios_app_path"));
            iosOptions.setAutomationName("XCUITest");
        }

        // ==========================================================
        // 2. EXECUTION LOGIC
        // ==========================================================

        if (mode.equalsIgnoreCase("grid")) {
            URL url = new URL(gridUrl);
            switch (browser.toLowerCase()) {
                case "chrome":  driver = new RemoteWebDriver(url, chromeOptions); break;
                case "firefox": driver = new RemoteWebDriver(url, firefoxOptions); break;
                case "edge":    driver = new RemoteWebDriver(url, edgeOptions); break;
                case "android": driver = new AndroidDriver(url, androidOptions); break;
                case "ios":     driver = new IOSDriver(url, iosOptions); break;
                default: throw new IllegalArgumentException("Invalid grid browser: " + browser);
            }

        } else if (mode.equalsIgnoreCase("cloud")) {
            // CLOUD EXECUTION
            if (browser.equalsIgnoreCase("android")) {
                androidOptions.setCapability("browserstack.user", ConfigReader.getProperty("cloud_username"));
                androidOptions.setCapability("browserstack.key", ConfigReader.getProperty("cloud_key"));
                androidOptions.setApp(ConfigReader.getProperty("cloud_android_app"));
                driver = new AndroidDriver(new URL(cloudUrl), androidOptions);

            } else if (browser.equalsIgnoreCase("ios")) {
                iosOptions.setCapability("browserstack.user", ConfigReader.getProperty("cloud_username"));
                iosOptions.setCapability("browserstack.key", ConfigReader.getProperty("cloud_key"));
                iosOptions.setApp(ConfigReader.getProperty("cloud_ios_app"));
                driver = new IOSDriver(new URL(cloudUrl), iosOptions);

            } else {
                ChromeOptions cloudOptions = new ChromeOptions();
                Map<String, Object> sauceOptions = new HashMap<>();
                sauceOptions.put("username", ConfigReader.getProperty("cloud_username"));
                sauceOptions.put("accessKey", ConfigReader.getProperty("cloud_key"));
                cloudOptions.setCapability("sauce:options", sauceOptions);
                driver = new RemoteWebDriver(new URL(cloudUrl), cloudOptions);
            }

        } else {
            // --- LOCAL EXECUTION ---
            // CRITICAL CHECK: If mode is 'mobile', we must NOT default to Chrome!
            if (mode.equalsIgnoreCase("mobile") || browser.equalsIgnoreCase("android")) {
                System.out.println("DEBUG: Starting Local Android Driver...");
                driver = new AndroidDriver(new URL(appiumUrl), androidOptions);
            } else {
                // WEB
                switch (browser.toLowerCase()) {
                    case "chrome":  driver = new ChromeDriver(chromeOptions); break;
                    case "firefox": driver = new FirefoxDriver(firefoxOptions); break;
                    case "edge":    driver = new EdgeDriver(edgeOptions); break;
                    default:
                        // Fallback logic if something is weird
                        if (browser.equalsIgnoreCase("android")) {
                            driver = new AndroidDriver(new URL(appiumUrl), androidOptions);
                        } else {
                            throw new IllegalArgumentException("Invalid local browser: " + browser);
                        }
                }
            }
        }
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(10));
        return driver;
    }
}
