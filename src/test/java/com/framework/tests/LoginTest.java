package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.driver.DriverManager;
import com.framework.pages.InventoryPage;
import com.framework.pages.LoginPage;
import com.framework.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LoginTest: Validates authentication flows across Web and Mobile platforms.
 * This is a core 'Smoke' test for the deployment pipeline.
 */
public class LoginTest extends BaseTest {

    @Test(groups = {"regression", "smoke"})
    public void testValidLogin() {
        // Initialize Page Objects
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        InventoryPage inventoryPage = new InventoryPage(DriverManager.getDriver());

        System.out.println("[INFO] Executing Valid Login Test...");

        // Step 1: Perform Login using credentials from Config
        loginPage.login(
                ConfigReader.getProperty("app_username"),
                ConfigReader.getProperty("app_password")
        );

        // Step 2: Assertion
        // The TestListener will automatically capture a screenshot if this fails.
        Assert.assertTrue(inventoryPage.isProductsHeaderDisplayed(),
                "CRITICAL FAILURE: Products header not displayed after login.");

        System.out.println("[PASS] Valid Login verified.");
    }
}
