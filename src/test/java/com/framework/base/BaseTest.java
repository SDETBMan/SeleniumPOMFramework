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

        // 1. INTELLIGENT CONFIG RESOLUTION
        // Priority:
        // 1. Maven/System Property (-Dbrowser=firefox)
        // 2. TestNG Parameter (xml)
        // 3. Config File (properties)

        // Check if System Property overrides everything (CI/CD best practice)
        if (System.getProperty("browser") != null) {
            browser = System.getProperty("browser");
        }
        // If no System/TestNG param (still default), fall back to Config
        else if (browser.equalsIgnoreCase("chrome") && ConfigReader.getProperty("browser") != null) {
            browser = ConfigReader.getProperty("browser");
        }

        System.out.println(">>> [SETUP] Target Browser: " + browser + " | Headless: " + headless);

        // 2. CREATE DRIVER
        WebDriver driver = DriverFactory.createInstance(browser, headless);

        // 3. FAIL-SAFE CHECK
        if (driver == null) {
            throw new RuntimeException(">>> FATAL: DriverFactory returned NULL. Check your Config/Grid status.");
        }

        // 4. SET TO MANAGER (Thread-Safe)
        DriverManager.setDriver(driver);
        System.out.println(">>> [SETUP] Driver assigned to DriverManager.");

        // 5. CONFIGURE DRIVER (Wait & Window)
        WebDriver currentDriver = DriverManager.getDriver();
        currentDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Only navigate/maximize if it's a Web test
        boolean isMobile = browser.equalsIgnoreCase("android") || browser.equalsIgnoreCase("ios");
        if (!isMobile) {
            String url = ConfigReader.getProperty("url");
            currentDriver.manage().window().maximize();
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
            DriverManager.unload(); // Removes the thread-local variable
        }
    }
}

