package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.driver.DriverManager;
import com.framework.pages.DashboardPage;
import com.framework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * DashboardTest: Validates post-login navigation, UI elements, and the logout lifecycle.
 * Utilizes DataProviders for broad user-persona coverage.
 */
public class DashboardTest extends BaseTest {

    @Test(groups = "regression", dataProvider = "loginData")
    public void testUserCanAccessDashboard(String username, String password) {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        DashboardPage dashboardPage = new DashboardPage(DriverManager.getDriver());

        System.out.println("[INFO] Executing Dashboard access test for user persona: " + username);

        // 1. Unified Login
        loginPage.login(username, password);

        // 2. Functional UI Assertions
        Assert.assertTrue(dashboardPage.isCartIconDisplayed(),
                "CRITICAL: Shopping cart icon missing on Dashboard for user: " + username);

        String headerText = dashboardPage.getWelcomeMessageText();
        System.out.println("[INFO] Dashboard Header captured: " + headerText);

        // Products is the standard header for Swag Labs (Web and Mobile)
        Assert.assertTrue(headerText.equalsIgnoreCase("Products"),
                "VALIDATION FAILED: Dashboard header did not match expected 'Products'. Found: " + headerText);
    }

    @Test(groups = "regression")
    public void testLogoutFlow() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        DashboardPage dashboardPage = new DashboardPage(DriverManager.getDriver());

        // 1. Establish session
        loginPage.login("standard_user", "secret_sauce");

        // 2. Execute Logout (Handles both Web JS-Menu and Mobile Native-Menu)
        dashboardPage.clickLogoutButton();

        // 3. Verify Session Termination (Returning to Login state)
        Assert.assertTrue(loginPage.isLoginButtonDisplayed(),
                "SESSION ERROR: Login button not visible after logout sequence.");
    }

    /**
     * Test Data Provider: Covers standard, UI-bug, and latency-heavy user accounts.
     */
    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        return new Object[][]{
                {"standard_user", "secret_sauce"},
                {"problem_user", "secret_sauce"},
                {"performance_glitch_user", "secret_sauce"}
        };
    }
}
