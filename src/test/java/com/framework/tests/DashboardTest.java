package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.driver.DriverManager;
import com.framework.pages.DashboardPage;
import com.framework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DashboardTest extends BaseTest {

    @Test(groups = "regression", dataProvider = "loginData")
    public void testUserCanAccessDashboard(String username, String password) {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());

        // 1. Log In
        loginPage.login(username, password); // Used the cleaner convenience method

        // 2. Dashboard Checks
        DashboardPage dashboardPage = new DashboardPage(DriverManager.getDriver());

        Assert.assertTrue(dashboardPage.isCartIconDisplayed(),
                "The Dashboard cart icon was not displayed after login for user: " + username);

        String headerText = dashboardPage.getWelcomeMessageText();
        System.out.println("Dashboard Header for " + username + ": " + headerText);
        Assert.assertFalse(headerText.isEmpty(), "Dashboard text should not be empty");
    }

    @Test(groups = "regression")
    public void testLogoutFlow() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        loginPage.login("standard_user", "secret_sauce");

        DashboardPage dashboardPage = new DashboardPage(DriverManager.getDriver());

        // The new clickLogoutButton() in DashboardPage opens the menu automatically.
        dashboardPage.clickLogoutButton();

        // Native mobile apps do not have URLs. We only verify the Login Button exists.
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
