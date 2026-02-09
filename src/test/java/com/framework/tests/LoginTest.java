package com.framework.tests;

import com.framework.base.BaseTest; // Ensure this matches your package
import com.framework.driver.DriverManager;
import com.framework.pages.InventoryPage;
import com.framework.pages.LoginPage;
import com.framework.utils.ConfigReader;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(groups = {"smoke"})
    public void testPageTitle(ITestContext context) {
        // SKIP FOR MOBILE: Native apps don't have HTML page titles
        String browser = context.getCurrentXmlTest().getParameter("browser");
        if (browser != null && (browser.equalsIgnoreCase("android") || browser.equalsIgnoreCase("ios"))) {
            System.out.println(">>> Skipping Page Title test for Mobile");
            return;
        }

        String title = DriverManager.getDriver().getTitle();
        System.out.println("Page Title is: " + title);
        Assert.assertEquals(title, "Swag Labs", "Title mismatch!");
    }

    @Test(groups = {"regression", "smoke"})
    public void testValidLogin() {
        // 1. Init Pages
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        InventoryPage inventoryPage = new InventoryPage(DriverManager.getDriver());

        // 2. Action (Using the cleaner 'login' convenience method)
        loginPage.login(
                ConfigReader.getProperty("username"),
                ConfigReader.getProperty("password")
        );

        // 3. Assertion (Platform Agnostic)
        // CORRECT: Checks for a visual element ("Products") instead of URL
        Assert.assertTrue(inventoryPage.isProductsHeaderDisplayed(),
                "Login failed! 'Products' header was not found.");
    }

    @Test(groups = {"regression"})
    public void testInvalidLogin() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());

        // 2. Action
        loginPage.login("locked_out_user", "wrong_pass");

        // 3. Assertion
        String actualError = loginPage.getErrorMessage();
        System.out.println("Error Message Found: " + actualError);

        Assert.assertTrue(actualError.contains("match"),
                "Expected error message not found! Actual: " + actualError);
    }
}
