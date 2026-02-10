package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.driver.DriverManager;
import com.framework.pages.InventoryPage;
import com.framework.pages.LoginPage;
import com.framework.utils.ConfigReader;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class LoginTest extends BaseTest {

    @Test(groups = {"regression", "smoke"})
    public void testValidLogin() {
        // 1. Initialize Page Objects
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        InventoryPage inventoryPage = new InventoryPage(DriverManager.getDriver());

        // 2. Perform Action (Clean & Readable)
        loginPage.login(
                ConfigReader.getProperty("app_username"),
                ConfigReader.getProperty("app_password")
        );

        // 3. Assertion (The Framework handles the rest)
        Assert.assertTrue(inventoryPage.isProductsHeaderDisplayed(),
                "Login failed! 'Products' header was not found.");
    }

    private void takeScreenshot(String name) {
        try {
            File srcFile = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
            File destFile = new File("target/screenshots/" + name + ".png");
            destFile.getParentFile().mkdirs();
            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println(">>> SCREENSHOT SAVED: " + destFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println(">>> FAILED TO TAKE SCREENSHOT: " + e.getMessage());
        }
    }
}
