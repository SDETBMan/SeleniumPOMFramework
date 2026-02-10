package com.framework.base;

import com.framework.driver.DriverFactory;
import com.framework.driver.DriverManager;
import com.framework.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.net.MalformedURLException;
import java.time.Duration;

public class BaseTest {

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "headless"})
    public void setUp(@Optional("chrome") String browser,
                      @Optional("false") String headless,
                      ITestContext context) throws MalformedURLException {

        long threadId = Thread.currentThread().getId();
        String testName = context.getCurrentXmlTest().getName();
        System.out.println(">>> [SETUP] Thread " + threadId + " | Test: " + testName);

        // ======================================================
        // 1. ROBUST CONFIG RESOLUTION (Decoupled Logic)
        // ======================================================

        // A. Resolve BROWSER (System Prop > Config > XML Default)
        if (System.getProperty("browser") != null) {
            browser = System.getProperty("browser");
        } else if (ConfigReader.getProperty("browser") != null && browser.equalsIgnoreCase("chrome")) {
            // Only fall back to config if System Prop is missing AND XML is default
            browser = ConfigReader.getProperty("browser");
        }

        // B. Resolve HEADLESS (System Prop > Config > XML Default)
        if (System.getProperty("headless") != null) {
            headless = System.getProperty("headless");
        } else if (ConfigReader.getProperty("headless") != null) {
            headless = ConfigReader.getProperty("headless");
        }

        System.out.println(">>> [SETUP] Target Browser: " + browser + " | Headless: " + headless);

        // ======================================================
        // 2. CREATE DRIVER
        // ======================================================
        WebDriver driver = DriverFactory.createInstance(browser, headless);

        if (driver == null) {
            throw new RuntimeException(">>> FATAL: DriverFactory returned NULL. Check your Config/Grid status.");
        }

        // 3. SET TO MANAGER
        DriverManager.setDriver(driver);

        // 4. CONFIGURE WINDOW & WAITS
        WebDriver currentDriver = DriverManager.getDriver();
        currentDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        boolean isMobile = browser.equalsIgnoreCase("android") || browser.equalsIgnoreCase("ios");
        if (!isMobile) {
            String url = ConfigReader.getProperty("url");

            // Handle Window Size properly based on mode
            if (Boolean.parseBoolean(headless)) {
                // Headless needs explicit size to "see" elements
                currentDriver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
            } else {
                // Headed mode should just maximize
                currentDriver.manage().window().maximize();
            }

            if (url != null) {
                System.out.println(">>> [NAV] Navigating to: " + url);
                currentDriver.get(url);
            }
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        System.out.println(">>> [TEARDOWN] Cleaning up Thread " + Thread.currentThread().getId());
        WebDriver driver = DriverManager.getDriver();

        if (driver != null) {
            driver.quit();
            DriverManager.unload();
        }
    }
}

