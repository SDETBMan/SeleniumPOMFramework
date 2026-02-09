package com.framework.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DashboardPage extends BasePage {

    // 1. LOCATORS
    private By cartIcon = By.className("shopping_cart_link");
    private By menuButton = By.id("react-burger-menu-btn");
    private By logoutLink = By.id("logout_sidebar_link");
    private By pageTitle = By.className("title");

    // Mobile
    private By mobileMenu = AppiumBy.accessibilityId("test-Menu");
    private By mobileLogout = AppiumBy.accessibilityId("test-LOGOUT");

    // 2. CONSTRUCTOR
    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    // 3. ACTION METHODS
    public boolean isCartIconDisplayed() {
        try {
            return driver.findElement(cartIcon).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickLogoutButton() {
        if (driver instanceof AndroidDriver || driver instanceof IOSDriver) {
            // Mobile Logic (Native)
            click(mobileMenu, "Mobile Menu");
            click(mobileLogout, "Mobile Logout");
        } else {
            // Web Logic (Robust JS Fix)
            click(menuButton, "Hamburger Menu");

            // Wait for the link to exist in the DOM (even if hidden/animating)
            WebElement logoutBtn = wait.until(ExpectedConditions.presenceOfElementLocated(logoutLink));

            // Force Click with JavaScript (Bypasses animation/obscured errors)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", logoutBtn);
            System.out.println("[WEB-ACTION] Force Clicked: Logout Link (JS)");
        }
    }

    public String getWelcomeMessageText() {
        return getText(pageTitle);
    }
}