package com.framework.pages;

import com.framework.utils.ConfigReader;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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

        // 1. Keep your centralized Explicit Wait (10 seconds)
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 2. NEW: Initialize the "Smart" Elements (PageFactory)
        // This tells Java to go find the elements marked with @AndroidFindBy or @FindBy
        String mode = ConfigReader.getProperty("execution_mode");

        // --- DEBUG PRINTS ---
        System.out.println("DEBUG: BasePage Initializing...");
        System.out.println("DEBUG: Mode is [" + mode + "]");
        // --------------------

        if (mode.equalsIgnoreCase("mobile")) {
            // MOBILE MAGIC: Use Appium's decorator with a 15-second "Patience Timer"
            // This fixes the "NoSuchElement" crash by waiting for the app to load.
            System.out.println("DEBUG: Setting Appium Wait to 15 SECONDS"); // Proof
            PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(15)), this);
        } else {
            // WEB: Use the standard Selenium initializer
            System.out.println("DEBUG: Using Standard Web Wait");
            PageFactory.initElements(driver, this);
        }
    }

    // ==========================================================
    // YOUR EXISTING WRAPPERS (Unchanged & Preserved)
    // ==========================================================

    // Wrapper for clicking
    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    // Wrapper for entering text
    protected void enterText(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
    }

    // Wrapper for getting text
    protected String getText(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }
}
