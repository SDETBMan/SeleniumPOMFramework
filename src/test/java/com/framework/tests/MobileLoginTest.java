package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.pages.mobile.MobileLoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * MobileLoginTest: Validates the Login functionality on Mobile.
 * * DESIGN PATTERN:
 * Uses "Page Object Model". The test doesn't find elements;
 * it asks the 'MobileLoginPage' to do actions.
 */
public class MobileLoginTest extends BaseTest {

    @Test
    public void testValidLogin() {
        // 1. Initialize the Page Object
        // We pass the 'driver' from BaseTest into the Page.
        MobileLoginPage loginPage = new MobileLoginPage(driver);

        // 2. Perform Actions (Steps)
        // Notice: These methods read like English sentences.
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.tapLoginButton();

        // 3. Validation (Assertions)
        // Always assert! A test without an assertion is just a script.
        Assert.assertTrue(loginPage.isDashboardVisible(),
                "Login Failed! Dashboard was not visible after tapping login.");
    }

    @Test
    public void testInvalidLogin() {
        MobileLoginPage loginPage = new MobileLoginPage(driver);

        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("WRONG_PASSWORD_123");
        loginPage.tapLoginButton();

        String errorMessage = loginPage.getErrorMessage();
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error message was not displayed!");
    }
}
