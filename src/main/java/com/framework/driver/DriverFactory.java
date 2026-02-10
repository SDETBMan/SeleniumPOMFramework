package com.framework.driver;

import com.framework.utils.ConfigReader;
import com.epam.healenium.SelfHealingDriver;
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

/**
 * DriverFactory: Universal logic for Web, Android, and iOS.
 * Integrated with Healenium for Self-Healing Web Tests.
 */
        public class DriverFactory {

        public static WebDriver createInstance(String browser, String headless) throws MalformedURLException {
        WebDriver delegate = null;

        String mode = ConfigReader.getProperty("execution_mode");
        // Fallback to "local" if mode is missing in config
        if (mode == null) mode = "local";

        String gridUrl = ConfigReader.getProperty("grid_url");
        String appiumUrl = "http://127.0.0.1:4723";

        System.out.println("=========================================");
        System.out.println("DEBUG: DriverFactory initialized.");
        System.out.println("DEBUG: Execution Mode: [" + mode + "]");
        System.out.println("DEBUG: Browser/Platform: [" + browser + "]");
        System.out.println("DEBUG: Headless Mode: [" + headless + "]");
        System.out.println("=========================================");

        boolean isHeadless = Boolean.parseBoolean(headless);

        // ==========================================================
        // 1. SETUP OPTIONS (The Fix is Here)
        // ==========================================================

        // WEB OPTIONS - CHROME
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--remote-allow-origins=*"); // Fix for Connection Failed errors
        if (isHeadless) {
        chromeOptions.addArguments("--headless=new"); // Modern Headless
        chromeOptions.addArguments("--window-size=1920,1080");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--no-sandbox");
        } else {
        chromeOptions.addArguments("--start-maximized"); // Force visible window
        }

        // WEB OPTIONS - FIREFOX
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        if (isHeadless) {
        firefoxOptions.addArguments("-headless");
        firefoxOptions.addArguments("--width=1920");
        firefoxOptions.addArguments("--height=1080");
        }

        // WEB OPTIONS - EDGE
        EdgeOptions edgeOptions = new EdgeOptions();
        if (isHeadless) {
        edgeOptions.addArguments("--headless");
        } else {
        edgeOptions.addArguments("--start-maximized");
        }

        // ANDROID OPTIONS
        UiAutomator2Options androidOptions = new UiAutomator2Options();
        if ("android".equalsIgnoreCase(browser)) {
        androidOptions.setDeviceName(ConfigReader.getProperty("android_device_name"));
        androidOptions.setApp(ConfigReader.getProperty("android_app_path"));
        androidOptions.setAutomationName("UiAutomator2");
        androidOptions.setCapability("appWaitActivity", "*");
        androidOptions.setCapability("appWaitDuration", 30000);
        }

        // iOS OPTIONS
        XCUITestOptions iosOptions = new XCUITestOptions();
        if ("ios".equalsIgnoreCase(browser)) {
        iosOptions.setDeviceName(ConfigReader.getProperty("ios_device_name"));
        iosOptions.setPlatformVersion(ConfigReader.getProperty("ios_version"));
        iosOptions.setApp(ConfigReader.getProperty("ios_app_path"));
        iosOptions.setAutomationName("XCUITest");
        }

        // ==========================================================
        // 2. EXECUTION LOGIC
        // ==========================================================

        if ("grid".equalsIgnoreCase(mode)) {
        // --- DOCKER GRID EXECUTION ---
        URL url = new URL(gridUrl);
        switch (browser.toLowerCase()) {
        case "chrome": delegate = new RemoteWebDriver(url, chromeOptions); break;
        case "firefox": delegate = new RemoteWebDriver(url, firefoxOptions); break;
        case "edge": delegate = new RemoteWebDriver(url, edgeOptions); break;
        case "android": delegate = new AndroidDriver(url, androidOptions); break;
        case "ios": delegate = new IOSDriver(url, iosOptions); break;
        default: throw new IllegalArgumentException("Invalid grid browser: " + browser);
        }

        } else if ("cloud".equalsIgnoreCase(mode)) {
        // --- BROWSERSTACK CLOUD EXECUTION ---
        String userName = System.getenv("BROWSERSTACK_USERNAME");
        if (userName == null) userName = ConfigReader.getProperty("cloud_username");

        String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");
        if (accessKey == null) accessKey = ConfigReader.getProperty("cloud_key");

        String authUrl = "https://" + userName + ":" + accessKey + "@hub-cloud.browserstack.com/wd/hub";
        URL remoteUrl = new URL(authUrl);

        if ("android".equalsIgnoreCase(browser)) {
        androidOptions.setApp(ConfigReader.getProperty("cloud_android_app"));
        delegate = new AndroidDriver(remoteUrl, androidOptions);
        } else if ("ios".equalsIgnoreCase(browser)) {
        iosOptions.setApp(ConfigReader.getProperty("cloud_ios_app"));
        delegate = new IOSDriver(remoteUrl, iosOptions);
        } else {
        // WEB CLOUD
        HashMap<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("userName", userName);
        bstackOptions.put("accessKey", accessKey);
        bstackOptions.put("os", "Windows");
        bstackOptions.put("osVersion", "11");
        bstackOptions.put("projectName", "QA Director Framework");
        bstackOptions.put("buildName", "Build 1.0");

        if ("firefox".equalsIgnoreCase(browser)) {
        firefoxOptions.setCapability("bstack:options", bstackOptions);
        delegate = new RemoteWebDriver(remoteUrl, firefoxOptions);
        } else {
        chromeOptions.setCapability("bstack:options", bstackOptions);
        delegate = new RemoteWebDriver(remoteUrl, chromeOptions);
        }
        }

        } else {
        // --- LOCAL EXECUTION ---
        if ("mobile".equalsIgnoreCase(mode) || "android".equalsIgnoreCase(browser)) {
        System.out.println("DEBUG: Starting Local Appium Driver...");
        delegate = new AndroidDriver(new URL(appiumUrl), androidOptions);
        } else {
        // WEB LOCAL
        switch (browser.toLowerCase()) {
        case "chrome":
        delegate = new ChromeDriver(chromeOptions);
        break;
        case "firefox":
        delegate = new FirefoxDriver(firefoxOptions);
        break;
        case "edge":
        delegate = new EdgeDriver(edgeOptions);
        break;
        default: throw new IllegalArgumentException("Invalid local browser: " + browser);
        }
        }
        }

        // 3. Set Timeouts
        if (delegate != null) {
        delegate.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(10));
        }

        // ==========================================================
        // 4. HEALENIUM INTEGRATION
        // ==========================================================
        boolean healingEnabled = Boolean.parseBoolean(System.getProperty("toggle.healing", "false"));
        boolean isMobile = "android".equalsIgnoreCase(browser) || "ios".equalsIgnoreCase(browser);

        if (delegate != null && healingEnabled && !isMobile) {
        try {
        System.out.println("DEBUG: Wrapping driver with Healenium...");
        return SelfHealingDriver.create(delegate);
        } catch (Exception e) {
        System.err.println("HEALENIUM ERROR: Backend missing. Using raw driver.");
        return delegate;
        }
        }

        return delegate;
        }
        }