package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.driver.DriverManager;
import com.framework.pages.DashboardPage;
import com.framework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DashboardTest extends BaseTest {

    // ✅ FIXED: Now strictly uses DriverManager.getDriver()
    // ✅ FIXED: Uses the 'username' and 'password' arguments from the DataProvider
    @Test(groups = "regression", dataProvider = "loginData")
    public void testUserCanAccessDashboard(String username, String password) {
        // 1. Initialize Login Page & Log In
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());

        // Use the arguments passed from the DataProvider!
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLoginButton();

        // 2. Transition to Dashboard Page
        DashboardPage dashboardPage = new DashboardPage(DriverManager.getDriver());

        // 3. Verify we are actually there
        Assert.assertTrue(dashboardPage.isCartIconDisplayed(),
                "The Dashboard cart icon was not displayed after login for user: " + username);

        // 4. Validate specific text (Optional but good practice)
        // Note: Swag Labs dashboard header is usually "Swag Labs" or "Products"
        String headerText = dashboardPage.getWelcomeMessageText(); // Ensure this method exists in DashboardPage
        System.out.println("Dashboard Header for " + username + ": " + headerText);
        Assert.assertFalse(headerText.isEmpty(), "Dashboard text should not be empty");
    }

    @Test(groups = "regression")
    public void testLogoutFlow() {
        // 1. Log In
        LoginPage loginPage = new LoginPage(DriverManager.getDriver()); // ✅ Fixed
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLoginButton();

        // 2. Log Out
        DashboardPage dashboardPage = new DashboardPage(DriverManager.getDriver()); // ✅ Fixed
        dashboardPage.clickMenuButton(); // You likely need to open the menu first!
        dashboardPage.clickLogoutButton(); // Ensure method name matches your Page Object

        // 3. Verify we are back on the Login Page
        String currentUrl = DriverManager.getDriver().getCurrentUrl(); // ✅ Fixed
        Assert.assertFalse(currentUrl.contains("inventory"), "User should be redirected away from dashboard after logout");
        Assert.assertTrue(loginPage.isLoginButtonDisplayed(), "Login button should be visible after logout");
    }

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        return new Object[][]{
                {"standard_user", "secret_sauce"},
                {"problem_user", "secret_sauce"},
                {"performance_glitch_user", "secret_sauce"}
        };
    }
}
