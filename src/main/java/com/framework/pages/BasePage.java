package com.framework.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        // 20s timeout is the "sweet spot" for GitHub Actions and Remote Grids
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // ==================================================
    // 1. WAITS & VISIBILITY
    // ==================================================

    protected void waitForVisibility(By locator) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            System.err.println("[ERROR] Element not visible after 20s: " + locator);
            throw e;
        }
    }

    /**
     * Perfect for "Cart Count" assertions.
     * Waits for the actual text value to change before proceeding.
     */
    protected boolean waitForTextToBePresent(By locator, String expectedText) {
        try {
            if(expectedText.isEmpty()) {
                return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            }
            return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * A "Safe" display check. Returns false instead of crashing if element is missing.
     */
    protected boolean isElementDisplayed(By locator) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================================================
    // 2. INTERACTION METHODS
    // ==================================================

    protected void click(By locator, String elementName) {
        try {
            // Wait for clickable ensures no "ElementClickIntercepted" errors
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));

            if (isMobile(driver)) {
                System.out.println("[MOBILE-ACTION] Tapping on: " + elementName);
            } else {
                highlightElement(element);
                System.out.println("[WEB-ACTION] Clicking on: " + elementName);
            }
            element.click();
        } catch (TimeoutException e) {
            System.err.println("[ERROR] Could not click " + elementName + " within timeout.");
            throw e;
        }
    }

    protected void click(By locator) {
        click(locator, locator.toString());
    }

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
            System.err.println("[ERROR] Could not type into " + elementName);
            throw e;
        }
    }

    /**
     * The Flakiness Killer: Automatically waits for visibility before grabbing text.
     * trim() prevents "Expected '1' but found ' 1 '" failures.
     */
    protected String getText(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText().trim();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to get text from locator: " + locator);
            return "";
        }
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
