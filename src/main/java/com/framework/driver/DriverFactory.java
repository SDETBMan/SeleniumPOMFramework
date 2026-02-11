package com.framework.driver;

import com.epam.healenium.SelfHealingDriver;
import com.framework.utils.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.MutableCapabilities;
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

/**
 * DriverFactory: Universal logic for Web, Android, and iOS.
 * Includes Self-Healing (Healenium) and Cloud (Sauce Labs) support.
 */
public class DriverFactory {

    public static WebDriver createInstance(String browser, String headless) throws MalformedURLException {
        WebDriver delegate = null;
        String mode = ConfigReader.getProperty("execution_mode");
        if (mode == null) mode = "local";

        System.out.println("=========================================");
        System.out.println("[INFO] DriverFactory Initialized");
        System.out.println("[INFO] Mode: " + mode);
        System.out.println("[INFO] Browser: " + browser);
        System.out.println("=========================================");

        boolean isHeadless = Boolean.parseBoolean(headless);

        // ==========================================================
        // 1. WEB OPTIONS (Chrome, Firefox, Edge)
        // ==========================================================
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--remote-allow-origins=*");
        if (isHeadless) {
            chromeOptions.addArguments("--headless=new");
            chromeOptions.addArguments("--window-size=1920,1080");
        }

        FirefoxOptions firefoxOptions = new FirefoxOptions();
        if (isHeadless) {
            firefoxOptions.addArguments("-headless");
            firefoxOptions.addArguments("--width=1920");
            firefoxOptions.addArguments("--height=1080");
        }

        EdgeOptions edgeOptions = new EdgeOptions();
        if (isHeadless) edgeOptions.addArguments("--headless");

        // ==========================================================
        // 2. MOBILE OPTIONS (Android, iOS)
        // ==========================================================
        UiAutomator2Options androidOptions = new UiAutomator2Options();
        if ("android".equalsIgnoreCase(browser)) {
            androidOptions.setDeviceName("Android Emulator");
            androidOptions.setAutomationName("UiAutomator2");
            androidOptions.setApp(ConfigReader.getProperty("android_app_path"));
        }

        XCUITestOptions iosOptions = new XCUITestOptions();
        if ("ios".equalsIgnoreCase(browser)) {
            iosOptions.setDeviceName("iPhone 15");
            iosOptions.setAutomationName("XCUITest");
            iosOptions.setApp(ConfigReader.getProperty("ios_app_path"));
        }

        // ==========================================================
        // 3. EXECUTION LOGIC (Local vs Cloud vs Grid)
        // ==========================================================
        /* * NOTE:
         * Cloud execution is fully implemented for scalability.
         * To enable:
         * 1. Set 'execution_mode=cloud' in config.properties
         * 2. Export SAUCE_USERNAME and SAUCE_ACCESS_KEY as Env Variables
         */
        if ("cloud".equalsIgnoreCase(mode)) {
            // --- SAUCE LABS CLOUD CONFIGURATION ---
            // Matches the secrets used in my GitHub Actions (.github/workflows/regression.yml)
            String username = System.getenv("SAUCE_USERNAME");
            String accessKey = System.getenv("SAUCE_ACCESS_KEY");
            String sauceUrl = "https://" + username + ":" + accessKey + "@ondemand.us-west-1.saucelabs.com:443/wd/hub";

            MutableCapabilities sauceOptions = new MutableCapabilities();
            sauceOptions.setCapability("username", username);
            sauceOptions.setCapability("accessKey", accessKey);
            sauceOptions.setCapability("build", "Director Suite Build");
            sauceOptions.setCapability("name", "Regression Test: " + browser);

            URL remoteUrl = new URL(sauceUrl);

            switch (browser.toLowerCase()) {
                case "chrome":
                    chromeOptions.setCapability("sauce:options", sauceOptions);
                    delegate = new RemoteWebDriver(remoteUrl, chromeOptions);
                    break;
                case "firefox":
                    firefoxOptions.setCapability("sauce:options", sauceOptions);
                    delegate = new RemoteWebDriver(remoteUrl, firefoxOptions);
                    break;
                case "android":
                    androidOptions.setCapability("sauce:options", sauceOptions);
                    delegate = new AndroidDriver(remoteUrl, androidOptions);
                    break;
                case "ios":
                    iosOptions.setCapability("sauce:options", sauceOptions);
                    delegate = new IOSDriver(remoteUrl, iosOptions);
                    break;
            }
        } else if ("local".equalsIgnoreCase(mode)) {
            // --- LOCAL EXECUTION ---
            if ("android".equalsIgnoreCase(browser)) {
                delegate = new AndroidDriver(new URL("http://127.0.0.1:4723"), androidOptions);
            } else if ("ios".equalsIgnoreCase(browser)) {
                delegate = new IOSDriver(new URL("http://127.0.0.1:4723"), iosOptions);
            } else {
                // Web Local
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
                }
            }
        }

        // ==========================================================
        // 4. HEALENIUM (Self-Healing Wrapper)
        // ==========================================================
        // Only wrap if it's NOT mobile (Healenium is primarily for Web)
        boolean isMobile = "android".equalsIgnoreCase(browser) || "ios".equalsIgnoreCase(browser);

        if (delegate != null && !isMobile) {
            try {
                // Returns the Self-Healing driver if backend is connected
                return SelfHealingDriver.create(delegate);
            } catch (Exception e) {
                System.out.println("[WARN] Healenium backend not found. Using standard driver.");
                return delegate;
            }
        }

        return delegate;
    }
}