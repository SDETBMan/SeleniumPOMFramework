package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.driver.DriverManager;
import com.framework.pages.LoginPage;
import com.framework.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(groups = {"smoke", "regression"})
    public void testPageTitle() {
        // ✅ ALWAYS use DriverManager.getDriver()
        String title = DriverManager.getDriver().getTitle();
        System.out.println("Page Title is: " + title);

        Assert.assertEquals(title, "Swag Labs", "Title mismatch!");
    }

    @Test(groups = {"regression"})
    public void testValidLogin() {
        // ✅ 1. Initialize Page with the Manager's driver
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());

        // ✅ 2. Use the Page Object methods (Clean & Readable)
        loginPage.enterUsername(ConfigReader.getProperty("username"));
        loginPage.enterPassword(ConfigReader.getProperty("password"));
        loginPage.clickLoginButton();

        // ✅ 3. Verify using the Manager's driver
        Assert.assertTrue(DriverManager.getDriver().getCurrentUrl().contains("inventory"),
                "User should be navigated to inventory page");
    }

    @Test
    public void testInvalidLogin() {
        // ✅ 1. Initialize Page
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());

        // ✅ 2. Perform Action
        loginPage.enterUsername("wrong_user");
        loginPage.enterPassword("wrong_pass");
        loginPage.clickLoginButton();

        // ✅ 3. Assertion
        // Ensure your LoginPage has a simple method to get the text
        // If your method is named differently (e.g. getErrorText), update it here.
        String actualError = loginPage.getErrorMessage();

        Assert.assertTrue(actualError.contains("Username and password do not match"),
                "Error message should be displayed. Found: " + actualError);
    }
}
