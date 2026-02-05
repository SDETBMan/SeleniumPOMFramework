package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.pages.DashboardPage;
import com.framework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

public class DashboardTest extends BaseTest {

    // Note the 'dataProvider' attribute linking to the method above
    @Test(groups = "regression", dataProvider = "loginData")
    public void testUserCanAccessDashboard(String username, String password) { // Arguments matching the data
        // 1. Initialize Login Page & Log In
        LoginPage loginPage = new LoginPage(driver);

        // Use real credentials for your test site (e.g., SwagLabs standard_user)
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLoginButton();

        // 2. Transition to Dashboard Page
        // Now that we clicked login, we expect to be on the Dashboard
        DashboardPage dashboardPage = new DashboardPage(driver);

        // 3. Verify we are actually there
        boolean isWelcomeVisible = dashboardPage.isWelcomeMessageDisplayed();

        // Assert: If this fails, the test stops here and marks as Failed
        Assert.assertTrue(isWelcomeVisible, "The Dashboard welcome message was not displayed after login!");

        // 4. (Optional) Validate specific text
        String welcomeText = dashboardPage.getWelcomeMessageText();
        System.out.println("Dashboard Header: " + welcomeText);
        Assert.assertFalse(welcomeText.isEmpty(), "Dashboard text should not be empty");
    }

    @Test(groups = "regression")
    public void testLogoutFlow() {
        // 1. Log In
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLoginButton();

        // 2. Log Out
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickLogout();

        // 3. Verify we are back on the Login Page
        // (Assuming LoginPage has a verify method, or checking URL)
        String currentUrl = driver.getCurrentUrl();
        Assert.assertFalse(currentUrl.contains("inventory"), "User should be redirected away from dashboard after logout");
    }

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        return new Object[][]{
                // { "username", "password", "expectedErrorMessage" (optional) }
                {"standard_user", "secret_sauce"},
                {"problem_user", "secret_sauce"},
                {"performance_glitch_user", "secret_sauce"}
        };
    }
}
