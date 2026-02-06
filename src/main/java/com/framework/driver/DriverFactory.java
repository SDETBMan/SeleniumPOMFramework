package com.framework.driver;


import com.framework.utils.ConfigReader;
import com.epam.healenium.SelfHealingDriver; // ✅ Healenium Import
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
 * Integrated with Healenium for Self-Healing Web Tests.
 */
public class DriverFactory {

    public static WebDriver createInstance(String browser, String headless) throws MalformedURLException {
        // We use 'delegate' to hold the raw driver before wrapping it
        WebDriver delegate = null;

        String mode = ConfigReader.getProperty("execution_mode");
        String gridUrl = ConfigReader.getProperty("grid_url");
        String cloudUrl = ConfigReader.getProperty("cloud_grid_url");
        String appiumUrl = "http://127.0.0.1:4723";

        // --- DEBUG PRINT ---
        System.out.println("=========================================");
        System.out.println("DEBUG: DriverFactory initialized.");
        System.out.println("DEBUG: Execution Mode from Config: [" + mode + "]");
        System.out.println("DEBUG: Browser/Platform Requested: [" + browser + "]");
        System.out.println("=========================================");

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
            androidOptions.setCapability("appWaitActivity", "*");
            androidOptions.setCapability("appWaitDuration", 30000);
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
                case "chrome":  delegate = new RemoteWebDriver(url, chromeOptions); break;
                case "firefox": delegate = new RemoteWebDriver(url, firefoxOptions); break;
                case "edge":    delegate = new RemoteWebDriver(url, edgeOptions); break;
                case "android": delegate = new AndroidDriver(url, androidOptions); break;
                case "ios":     delegate = new IOSDriver(url, iosOptions); break;
                default: throw new IllegalArgumentException("Invalid grid browser: " + browser);
            }

        } else if (mode.equalsIgnoreCase("cloud")) {
            // CLOUD EXECUTION
            if (browser.equalsIgnoreCase("android")) {
                androidOptions.setCapability("browserstack.user", ConfigReader.getProperty("cloud_username"));
                androidOptions.setCapability("browserstack.key", ConfigReader.getProperty("cloud_key"));
                androidOptions.setApp(ConfigReader.getProperty("cloud_android_app"));
                delegate = new AndroidDriver(new URL(cloudUrl), androidOptions);

            } else if (browser.equalsIgnoreCase("ios")) {
                iosOptions.setCapability("browserstack.user", ConfigReader.getProperty("cloud_username"));
                iosOptions.setCapability("browserstack.key", ConfigReader.getProperty("cloud_key"));
                iosOptions.setApp(ConfigReader.getProperty("cloud_ios_app"));
                delegate = new IOSDriver(new URL(cloudUrl), iosOptions);

            } else {
                ChromeOptions cloudOptions = new ChromeOptions();
                Map<String, Object> sauceOptions = new HashMap<>();
                sauceOptions.put("username", ConfigReader.getProperty("cloud_username"));
                sauceOptions.put("accessKey", ConfigReader.getProperty("cloud_key"));
                cloudOptions.setCapability("sauce:options", sauceOptions);
                delegate = new RemoteWebDriver(new URL(cloudUrl), cloudOptions);
            }

        } else {
            // --- LOCAL EXECUTION ---
            if (mode.equalsIgnoreCase("mobile") || browser.equalsIgnoreCase("android")) {
                System.out.println("DEBUG: Starting Local Android Driver...");
                delegate = new AndroidDriver(new URL(appiumUrl), androidOptions);
            } else {
                // WEB
                switch (browser.toLowerCase()) {
                    case "chrome":  delegate = new ChromeDriver(chromeOptions); break;
                    case "firefox": delegate = new FirefoxDriver(firefoxOptions); break;
                    case "edge":    delegate = new EdgeDriver(edgeOptions); break;
                    default:
                        if (browser.equalsIgnoreCase("android")) {
                            delegate = new AndroidDriver(new URL(appiumUrl), androidOptions);
                        } else {
                            throw new IllegalArgumentException("Invalid local browser: " + browser);
                        }
                }
            }
        }

        // 3. Set Timeouts on the raw driver
        if (delegate != null) {
            delegate.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(10));
        }

        // ==========================================================
        // 4. HEALENIUM INTEGRATION (The "Magic" Step) 🎩
        // ==========================================================
        // We only wrap Web Browsers. Mobile/Appium often requires a different setup.
        boolean isMobile = browser.equalsIgnoreCase("android") || browser.equalsIgnoreCase("ios");

        if (delegate != null && !isMobile) {
            System.out.println("DEBUG: Wrapping driver with Healenium for Self-Healing...");
            return SelfHealingDriver.create(delegate);
        }

        return delegate;
    }
}
