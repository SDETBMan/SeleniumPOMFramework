package com.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DashboardPage extends BasePage {

    // 1. Locators (Private: Encapsulation)
    private By welcomeMessage = By.id("welcome-message");
    private By logoutButton = By.xpath("//button[text()='Logout']");
    private By settingsLink = By.cssSelector("a[href='/settings']");

    // 2. Constructor
    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    // 3. Actions (Public: The API for your test)

    /**
     * Verifies if the dashboard is loaded by checking the welcome message.
     * @return true if visible
     */
    public boolean isWelcomeMessageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(welcomeMessage));
            return driver.findElement(welcomeMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the text of the welcome message for validation.
     */
    public String getWelcomeMessageText() {
        return driver.findElement(welcomeMessage).getText();
    }

    /**
     * Logs the user out and returns the LoginPage object.
     * (Fluent Interface pattern)
     */
    public LoginPage clickLogout() {
        click(logoutButton);
        return new LoginPage(driver);
    }
}
