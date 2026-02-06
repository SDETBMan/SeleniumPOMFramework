package com.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage extends BasePage {

    // 1. Private Locators (By)
    private By usernameField = By.id("user-name");
    private By passwordField = By.id("password");
    private By loginButton = By.id("login-button");
    private By errorMessage = By.cssSelector("h3[data-test='error']");

    // Constructor passes driver to BasePage
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // 2. Public Actions

    // Type the username
    public void enterUsername(String username) {
        // Correct: Find the element using the locator, then type
        driver.findElement(usernameField).sendKeys(username);
    }

    // Type the password
    public void enterPassword(String password) {
        // FIXED: This was accidentally clicking the button! Now it sends keys.
        driver.findElement(passwordField).sendKeys(password);
    }

    // Convenience method for full login
    public void clickLogin(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    public void clickLoginButton() {
        // Use your BasePage helper 'click' if available, or raw Selenium:
        driver.findElement(loginButton).click();
    }

    public String getErrorMessage() {
        return driver.findElement(errorMessage).getText();
    }

    /**
     * Verifies if the Login button is displayed.
     * Used to confirm successful logout.
     */
    public boolean isLoginButtonDisplayed() {
        try {
            // ✅ FIX: Use 'driver.findElement' to turn the 'By' into a 'WebElement'
            return driver.findElement(loginButton).isDisplayed();
        } catch (Exception e) {
            // If the element is not found, it's not displayed
            return false;
        }
    }
}
