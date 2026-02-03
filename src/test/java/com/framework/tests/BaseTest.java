package com.framework.tests;

import com.framework.driver.DriverFactory;
import com.framework.driver.DriverManager;
import com.framework.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional; // Added this
import org.testng.annotations.Parameters; // Added this
import java.net.MalformedURLException;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    @Parameters({"browser", "headless"}) // Must match the XML names
    public void setUp(@Optional("chrome") String browser, @Optional("false") String headless) throws MalformedURLException {
        // Pass both to the factory
        driver = DriverFactory.createInstance(browser, headless);
        DriverManager.setDriver(driver);

        String url = ConfigReader.getProperty("url");
        driver.get(url);
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown() {
        if (DriverManager.getDriver() != null) {
            DriverManager.getDriver().quit();
            DriverManager.unload();
        }
    }
}
