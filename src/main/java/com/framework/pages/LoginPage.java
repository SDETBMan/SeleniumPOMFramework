package com.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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
        // 'writeText' is a helper in your BasePage (or use driver.findElement...sendKeys)
        driver.findElement(usernameField).sendKeys(username);
    }

    // Type the password
    public void enterPassword(String password) {
        driver.findElement(loginButton).click();
    }

    public void clickLogin(String username, String password) {
        enterText(usernameField, username);
        enterText(passwordField, password);

    }

    public void clickLoginButton() {
        click(loginButton);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }
}
