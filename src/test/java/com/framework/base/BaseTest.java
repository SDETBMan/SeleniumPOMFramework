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

/**
 * BaseTest: The "Parent" class for all test classes.
 * RESPONSIBILITY:
 * 1. Initialize the Driver (Web or Mobile) via the Factory.
 * 2. Manage the ThreadLocal context via DriverManager.
 * 3. Handle Conditional Logic (Web actions vs. Mobile actions).
 * 4. Cleanup after tests.
 */
public class BaseTest {

    // Protected so child test classes (like 'MobileLoginTest') can access the driver directly.
    protected WebDriver driver;

    /**
     * setUp: Runs automatically before EVERY @Test method.
     * @param browser Passed from testng.xml. Defaults to "chrome" if running a single file manually.
     * @param headless Passed from testng.xml. Defaults to "false".
     */
    @BeforeMethod
    @Parameters({"browser", "headless"})
    public void setUp(@Optional("chrome") String browser, @Optional("false") String headless) throws MalformedURLException {

        // 1. PRIORITY CHECK (The Fix)
        // If we are running from IntelliJ (where browser defaults to "chrome"),
        // but the Config File says we want "mobile", we MUST update the browser variable
        // BEFORE sending it to the DriverFactory.
        String configMode = ConfigReader.getProperty("execution_mode");
        String configBrowser = ConfigReader.getProperty("browser");

        if (configMode != null && configMode.equalsIgnoreCase("mobile")) {
            // Force the browser to be what the config says (e.g., "android")
            browser = configBrowser;
        }

        // 2. Create the Driver Instance
        // Now calling this with the CORRECT browser type (e.g., "android")
        driver = DriverFactory.createInstance(browser, headless);

        // 3. Set the Driver in ThreadLocal
        // This ensures thread safety when running tests in parallel.
        DriverManager.setDriver(driver);

        // 4. Define "Is this Mobile?" Logic
        // Since we updated the 'browser' variable in Step 1, this logic is now simple.
        boolean isMobile = browser.equalsIgnoreCase("android") ||
                browser.equalsIgnoreCase("ios");

        // 5. Set Global Wait
        // Mobile emulators are slower than browsers. A 10-second default wait
        // helps prevent "Element Not Found" errors during screen transitions.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // 6. Web-Specific Startup Logic
        // CRITICAL: We CANNOT run .get(url) or .maximize() on a native mobile app.
        if (!isMobile) {
            String url = ConfigReader.getProperty("url");

            // Navigate to the website ONLY if we are NOT on mobile
            if (url != null) {
                driver.get(url);
            }

            // Maximize browser window (Standard for Web, invalid for Mobile)
            driver.manage().window().maximize();
        }
        // NOTE: If it IS mobile, Appium automatically launches the app based on the
        // 'app' capability in DriverFactory. No extra code needed here!
    }

    /**
     * tearDown: Runs automatically after EVERY @Test method.
     * RESPONSIBILITY: Close the browser/app and clean up memory.
     */
    @AfterMethod
    public void tearDown() {
        // Check if the driver exists to avoid NullPointerExceptions
        if (DriverManager.getDriver() != null) {

            // 1. Quit the Driver
            // Closes the Browser window or Stops the Appium session.
            DriverManager.getDriver().quit();

            // 2. Remove from ThreadLocal
            // CRITICAL: If you don't do this, the thread keeps a reference to the dead driver,
            // which causes memory leaks in large test suites.
            DriverManager.unload();
        }
    }
}

