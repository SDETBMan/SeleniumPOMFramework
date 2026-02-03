package com.framework.tests;

import com.framework.driver.DriverManager;
import com.framework.pages.LoginPage;
import com.framework.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(groups = {"smoke", "regression"})
    public void testPageTitle() {
        // Since BaseTest handles the setup, we just use the driver here
        String title = DriverManager.getDriver().getTitle();
        System.out.println("Page Title is: " + title);

        // Verification
        Assert.assertEquals(title, "Swag Labs", "Title mismatch! Check if the page loaded.");
    }

    @Test(groups = {"regression"})
    public void testValidLogin() {
        LoginPage loginPage = new LoginPage(driver);

        // Read sensitive data from Config instead of hardcoding
        loginPage.login(
                ConfigReader.getProperty("username"),
                ConfigReader.getProperty("password")
        );

        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"),
                "User should be navigated to inventory page");
    }

    @Test
    public void testInvalidLogin() {
        LoginPage loginPage = new LoginPage(driver);

        // Perform Action
        loginPage.login("wrong_user", "wrong_pass");

        // Assertion
        String actualError = loginPage.getErrorMessage();
        Assert.assertTrue(actualError.contains("Username and password do not match"),
                "Error message should be displayed");
    }
}
