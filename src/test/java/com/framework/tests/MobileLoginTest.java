package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.driver.DriverManager;
import com.framework.pages.mobile.MobileLoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MobileLoginTest extends BaseTest {

    @Test
    public void testValidLogin() {
        // Use DriverManager.getDriver()
        MobileLoginPage loginPage = new MobileLoginPage(DriverManager.getDriver());

        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.tapLoginButton();

        Assert.assertTrue(loginPage.isDashboardVisible(),
                "Dashboard should be visible after valid login!");
    }

    @Test
    public void testInvalidLogin() {
        // Use DriverManager.getDriver()
        MobileLoginPage loginPage = new MobileLoginPage(DriverManager.getDriver());

        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("WRONG_PASSWORD");
        loginPage.tapLoginButton();

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error message was not displayed!");
    }
}
