package com.framework.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    // Constructor
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Initialize "Smart" Elements (PageFactory)
        if (isMobile(driver)) {
            PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(15)), this);
        } else {
            PageFactory.initElements(driver, this);
        }
    }

    // ==================================================
    // 1. MISSING METHOD (The Fix for InventoryPage)
    // ==================================================

    /**
     * Waits for an element to be visible. Used for assertions.
     */
    protected void waitForVisibility(By locator) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            System.err.println("❌ Element not visible: " + locator);
            throw e;
        }
    }

    // ==================================================
    // 2. INTERACTION METHODS (The Fix for LoginPage)
    // ==================================================

    /**
     * Smart Click with Logging
     */
    protected void click(By locator, String elementName) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));

            if (isMobile(driver)) {
                System.out.println("[MOBILE-ACTION] Tapping on: " + elementName);
            } else {
                System.out.println("[WEB-ACTION] Clicking on: " + elementName);
                highlightElement(element);
            }
            element.click();
        } catch (TimeoutException e) {
            System.err.println("❌ ERROR: Could not click " + elementName);
            throw e;
        }
    }

    // Overload: Allows calling click(locator) without a name string
    protected void click(By locator) {
        click(locator, locator.toString());
    }

    /**
     * Smart Typing with Keyboard Handling
     * Renamed to 'enterText' to match your LoginPage code
     */
    protected void enterText(By locator, String text, String elementName) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            element.clear();
            element.sendKeys(text);

            if (isMobile(driver)) {
                hideKeyboard();
            }
            System.out.println("[" + getPlatform() + "] Entered '" + text + "' into: " + elementName);
        } catch (TimeoutException e) {
            System.err.println("❌ ERROR: Could not type into " + elementName);
            throw e;
        }
    }

    // Overload: Allows calling enterText(locator, text) without a name string
    protected void enterText(By locator, String text) {
        enterText(locator, text, locator.toString());
    }

    protected String getText(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }

    // ==================================================
    // 3. HELPER METHODS
    // ==================================================

    private boolean isMobile(WebDriver driver) {
        return driver instanceof AndroidDriver || driver instanceof IOSDriver;
    }

    private String getPlatform() {
        if (driver instanceof AndroidDriver) return "ANDROID";
        if (driver instanceof IOSDriver) return "IOS";
        return "WEB";
    }

    private void hideKeyboard() {
        if (driver instanceof AndroidDriver) {
            try {
                ((AndroidDriver) driver).hideKeyboard();
            } catch (Exception ignored) {}
        }
    }

    private void highlightElement(WebElement element) {
        if (!isMobile(driver)) {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid red'", element);
            } catch (Exception ignored) {}
        }
    }
}
