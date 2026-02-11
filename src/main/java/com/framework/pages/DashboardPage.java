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

    // 1. DYNAMIC LOCATORS (Initialized in Constructor)
    private By cartIcon;
    private By menuButton;
    private By logoutLink;
    private By pageTitle;

    public DashboardPage(WebDriver driver) {
        super(driver);
        initLocators();
    }

    /**
     * Initialize locators based on Platform (Web vs. Mobile).
     */
    private void initLocators() {
        boolean isNativeMobile = (driver instanceof AndroidDriver) || (driver instanceof IOSDriver);

        if (isNativeMobile) {
            // MOBILE (Accessibility IDs)
            cartIcon = AppiumBy.accessibilityId("test-Cart");
            menuButton = AppiumBy.accessibilityId("test-Menu");
            logoutLink = AppiumBy.accessibilityId("test-LOGOUT");
            // Android title is usually a TextView "PRODUCTS"
            pageTitle = By.xpath("//android.widget.TextView[@text='PRODUCTS']");
        } else {
            // WEB (Standard CSS/ID)
            cartIcon = By.className("shopping_cart_link");
            menuButton = By.id("react-burger-menu-btn");
            logoutLink = By.id("logout_sidebar_link");
            pageTitle = By.className("title");
        }
    }

    // ==================================================
    // 2. ACTION METHODS
    // ==================================================

    public boolean isCartIconDisplayed() {
        try {
            return driver.findElement(cartIcon).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickLogoutButton() {
        // 1. Open Menu
        click(menuButton, "Hamburger Menu");

        // 2. Click Logout (Platform Specific Logic)
        if (driver instanceof AndroidDriver || driver instanceof IOSDriver) {
            // Mobile: Just tap it
            click(logoutLink, "Logout Link");
        } else {
            // Web: Handle Animation Lag with JS Force Click
            try {
                WebElement logoutBtn = wait.until(ExpectedConditions.presenceOfElementLocated(logoutLink));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", logoutBtn);
                System.out.println("[WEB-ACTION] Force Clicked: Logout Link (JS)");
            } catch (Exception e) {
                // Fallback to standard click if JS fails
                click(logoutLink, "Logout Link");
            }
        }
    }

    public String getWelcomeMessageText() {
        return getText(pageTitle);
    }
}