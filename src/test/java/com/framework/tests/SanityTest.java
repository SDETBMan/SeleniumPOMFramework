package com.framework.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * SanityTest: A "bare-metal" Selenium test independent of the framework's BaseTest.
 * Used for environment verification and troubleshooting driver/binary issues.
 */
public class SanityTest {

    @Test(groups = "sanity")
    public void rawSeleniumTest() {
        System.out.println("[INFO] Starting Bare-Metal Sanity Check...");

        // 1. Manual Driver Setup (Bypassing DriverFactory)
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        WebDriver driver = new ChromeDriver(options);

        try {
            // 2. Navigation
            driver.get("https://www.saucedemo.com/");
            System.out.println("[INFO] Page Title: " + driver.getTitle());

            // 3. Raw Element Interaction
            driver.findElement(By.id("user-name")).sendKeys("standard_user");
            driver.findElement(By.id("password")).sendKeys("secret_sauce");
            driver.findElement(By.id("login-button")).click();

            // 4. Explicit Synchronization
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title")));

            // 5. Assertions
            String headerText = header.getText();
            System.out.println("[INFO] Header Found: " + headerText);

            Assert.assertEquals(headerText, "Products", "Sanity Check Failed: Dashboard header mismatch.");

        } catch (Exception e) {
            System.err.println("[FATAL] Sanity test failed at URL: " + driver.getCurrentUrl());
            Assert.fail("Environment Sanity Check Failed: " + e.getMessage());
        } finally {
            // 6. Manual Teardown
            if (driver != null) {
                driver.quit();
                System.out.println("[INFO] Sanity Test Finalized.");
            }
        }
    }
}
