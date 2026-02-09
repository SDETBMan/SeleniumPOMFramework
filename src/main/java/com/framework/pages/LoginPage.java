package com.framework.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    // Locators (We initialize them in the constructor now)
    private By usernameField;
    private By passwordField;
    private By loginButton;
    private By errorMessage;

    public LoginPage(WebDriver driver) {
        super(driver);

        // --- HYBRID LOCATOR STRATEGY ---
        // Check if we are running on a Mobile App
        boolean isNativeMobile = (driver instanceof AndroidDriver) || (driver instanceof IOSDriver);

        if (isNativeMobile) {
            // SWAG LABS NATIVE APP LOCATORS (Accessibility IDs)
            usernameField = AppiumBy.accessibilityId("test-Username");
            passwordField = AppiumBy.accessibilityId("test-Password");
            loginButton   = AppiumBy.accessibilityId("test-LOGIN");
            errorMessage  = AppiumBy.accessibilityId("test-Error message");
        } else {
            // SWAG LABS WEB LOCATORS (Standard IDs)
            usernameField = By.id("user-name");
            passwordField = By.id("password");
            loginButton   = By.id("login-button");
            errorMessage  = By.cssSelector("h3[data-test='error']");
        }
    }

    // 2. Actions (Unchanged - The "What" stays the same, only the "How" changed above)

    public void enterUsername(String username) {
        enterText(usernameField, username, "Username Field");
    }

    public void enterPassword(String password) {
        enterText(passwordField, password, "Password Field");
    }

    public void clickLoginButton() {
        click(loginButton, "Login Button");
    }

    public String getErrorMessage() {
        // 1. Try standard getText (Works for Web)
        String text = getText(errorMessage);

        // 2. Mobile Fallback Logic (If text is empty)
        if (text == null || text.isEmpty()) {

            if (driver instanceof AndroidDriver) {
                // ANDROID FIX: The ID points to a ViewGroup container.
                // We must find the child TextView to get the actual text.
                try {
                    text = driver.findElement(errorMessage)
                            .findElement(By.className("android.widget.TextView"))
                            .getText();
                } catch (Exception e) {
                    System.out.println("DEBUG: Failed to find child TextView on Android: " + e.getMessage());
                }
            }
            else if (driver instanceof IOSDriver) {
                // IOS FIX: Text is often hidden in 'label' or 'name' attributes
                try {
                    text = driver.findElement(errorMessage).getAttribute("label");
                    if (text == null || text.isEmpty()) {
                        text = driver.findElement(errorMessage).getAttribute("name");
                    }
                } catch (Exception e) {
                    System.out.println("DEBUG: Failed to retrieve iOS attribute: " + e.getMessage());
                }
            }
        }
        return text;
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    public boolean isLoginButtonDisplayed() {
        try {
            return driver.findElement(loginButton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
