package com.framework.base;

import com.framework.driver.DriverFactory;
import com.framework.driver.DriverManager;
import com.framework.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.net.MalformedURLException;
import java.time.Duration;

public class BaseTest {

    // We do NOT use 'protected WebDriver driver' here anymore to prevent confusion.
    // Tests must use DriverManager.getDriver()

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "headless"})
    public void setUp(@Optional("chrome") String browser, @Optional("false") String headless) throws MalformedURLException {
        System.out.println(">>> STARTING SETUP: Thread ID " + Thread.currentThread().getId());

        // 1. Config Override
        String configMode = ConfigReader.getProperty("execution_mode");
        String configBrowser = ConfigReader.getProperty("browser");
        if (configMode != null && configMode.equalsIgnoreCase("mobile")) {
            browser = configBrowser;
        }

        // 2. Create Driver
        System.out.println(">>> Creating Driver Instance for: " + browser);
        WebDriver driver = DriverFactory.createInstance(browser, headless);

        // 3. Safety Check
        if (driver == null) {
            System.err.println(">>> FATAL: DriverFactory returned NULL!");
            throw new RuntimeException("DriverFactory returned null for browser: " + browser);
        }
        System.out.println(">>> Driver Created Successfully: " + driver);

        // 4. Set to Manager (THE CRITICAL STEP)
        System.out.println(">>> Setting Driver in DriverManager...");
        DriverManager.setDriver(driver);

        // 5. Verification
        if (DriverManager.getDriver() == null) {
            System.err.println(">>> FATAL: DriverManager.getDriver() is NULL immediately after setting it!");
        } else {
            System.out.println(">>> DriverManager confirms Driver is set: " + DriverManager.getDriver());
        }

        // 6. Config & Navigation
        DriverManager.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        boolean isMobile = browser.equalsIgnoreCase("android") || browser.equalsIgnoreCase("ios");
        if (!isMobile) {
            String url = ConfigReader.getProperty("url");
            if (url != null) {
                System.out.println(">>> Navigating to URL: " + url);
                DriverManager.getDriver().get(url);
            }
            DriverManager.getDriver().manage().window().maximize();
        }
        System.out.println(">>> SETUP COMPLETE");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        System.out.println(">>> TEARING DOWN: Thread ID " + Thread.currentThread().getId());
        WebDriver driver = DriverManager.getDriver();
        if (driver != null) {
            driver.quit();
            DriverManager.unload();
            System.out.println(">>> Driver Quit and Unloaded.");
        } else {
            System.out.println(">>> Driver was ALREADY NULL during tearDown.");
        }
    }
}

