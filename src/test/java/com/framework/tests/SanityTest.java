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

public class SanityTest {

    @Test
    public void rawSeleniumTest() {
        System.out.println(">>> SANITY TEST STARTING <<<");

        // 1. Setup Raw Driver (No BaseTest)
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Keep headless for now
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        WebDriver driver = new ChromeDriver(options);

        try {
            // 2. Go to URL
            driver.get("https://www.saucedemo.com/");
            System.out.println(">>> OPENED: " + driver.getTitle());

            // 3. Login (Hardcoded)
            driver.findElement(By.id("user-name")).sendKeys("standard_user");
            driver.findElement(By.id("password")).sendKeys("secret_sauce");
            driver.findElement(By.id("login-button")).click();
            System.out.println(">>> CLICKED LOGIN");

            // 4. Wait for Products
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title")));

            // 5. Verify
            boolean isDisplayed = header.isDisplayed();
            System.out.println(">>> HEADER FOUND? " + isDisplayed);
            System.out.println(">>> TEXT IS: " + header.getText());

            Assert.assertTrue(isDisplayed);

        } catch (Exception e) {
            System.out.println(">>> SANITY FAILED! URL: " + driver.getCurrentUrl());
            e.printStackTrace();
            Assert.fail("Sanity Check Crashed: " + e.getMessage());
        } finally {
            driver.quit();
        }
    }
}
