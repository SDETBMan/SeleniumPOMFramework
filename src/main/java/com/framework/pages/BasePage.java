package com.framework.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    // Constructor
    public BasePage(WebDriver driver) {
        this.driver = driver;

        // 1. Centralized Explicit Wait (10 seconds)
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 2. Initialize "Smart" Elements (PageFactory)
        // Kept your existing logic, but made the check dynamic based on the driver type
        // This is safer than ConfigReader because the driver is the source of truth.
        if (isMobile(driver)) {
            System.out.println("DEBUG: Initializing Appium Field Decorator (Mobile)");
            PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(15)), this);
        } else {
            System.out.println("DEBUG: Initializing Standard PageFactory (Web)");
            PageFactory.initElements(driver, this);
        }
    }

    // ==================================================
    // 1. UNIVERSAL INTERACTION METHODS (The Upgrade)
    // ==================================================

    /**
     * Smart Click: Logs "Tap" for Mobile, "Click" for Web.
     * @param locator The By locator (e.g., By.id("..."))
     * @param elementName Description for the logs (e.g., "Login Button")
     */
    protected void click(By locator, String elementName) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));

            if (isMobile(driver)) {
                System.out.println("[MOBILE-ACTION] Tapping on: " + elementName);
            } else {
                System.out.println("[WEB-ACTION] Clicking on: " + elementName);
                highlightElement(element); // Visual debug for Web
            }

            element.click();

        } catch (TimeoutException e) {
            System.err.println("❌ ERROR: Could not click " + elementName + " after 10 seconds.");
            throw e;
        }
    }

    /**
     * Smart Typing: Automatically hides keyboard on mobile after typing.
     */
    protected void enterText(By locator, String text, String elementName) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            element.clear();
            element.sendKeys(text);

            if (isMobile(driver)) {
                hideKeyboard();
            }

            System.out.println("[" + getPlatform() + "] Entered text '" + text + "' into: " + elementName);

        } catch (TimeoutException e) {
            System.err.println("❌ ERROR: Could not type into " + elementName);
            throw e;
        }
    }

    protected String getText(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }

    // ==================================================
    // 2. HELPER METHODS (The "Brain")
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
            } catch (Exception ignored) {
                // Soft keyboard might not be present, ignore.
            }
        }
        // iOS auto-hides on tap usually, or requires specific "Done" button logic
    }

    private void highlightElement(WebElement element) {
        if (!isMobile(driver)) {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid red'", element);
            } catch (Exception ignored) {}
        }
    }
}
