package com.framework.driver;

import com.framework.utils.ConfigReader;
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

public class DriverFactory {

    public static WebDriver createInstance(String browser, String headless) throws MalformedURLException {
        WebDriver driver = null;

        String mode = ConfigReader.getProperty("execution_mode");
        String gridUrl = ConfigReader.getProperty("grid_url");
        boolean isHeadless = Boolean.parseBoolean(headless);

        // 1. Setup Options for ALL browsers (Applied to both Local and Grid)
        ChromeOptions chromeOptions = new ChromeOptions();
        if (isHeadless) chromeOptions.addArguments("--headless");

        FirefoxOptions firefoxOptions = new FirefoxOptions();
        if (isHeadless) firefoxOptions.addArguments("-headless");

        EdgeOptions edgeOptions = new EdgeOptions();
        if (isHeadless) edgeOptions.addArguments("--headless");

        // 2. Execution Logic
        if (mode.equalsIgnoreCase("grid")) {
            URL url = new URL(gridUrl);
            switch (browser.toLowerCase()) {
                case "chrome":
                    driver = new RemoteWebDriver(url, chromeOptions);
                    break;
                case "firefox":
                    driver = new RemoteWebDriver(url, firefoxOptions);
                    break;
                case "edge":
                    driver = new RemoteWebDriver(url, edgeOptions);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid browser: " + browser);
            }
        } else {
            // Local Execution
            switch (browser.toLowerCase()) {
                case "chrome":
                    driver = new ChromeDriver(chromeOptions);
                    break;
                case "firefox":
                    driver = new FirefoxDriver(firefoxOptions);
                    break;
                case "edge":
                    driver = new EdgeDriver(edgeOptions);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid browser: " + browser);
            }
        }
        return driver;
    }
}
