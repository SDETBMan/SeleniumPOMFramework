package com.framework.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InventoryPage extends BasePage {

    private By productHeader;

    public InventoryPage(WebDriver driver) {
        super(driver);

        // Define the locator for the "Products" text
        if (driver instanceof AndroidDriver || driver instanceof IOSDriver) {
            // Mobile usually has a specific accessibility ID or text
            productHeader = AppiumBy.xpath("//*[@text='Products']");
        } else {
            // Web: The header span with class 'title'
            productHeader = By.className("title");
        }
    }

    public boolean isProductsHeaderDisplayed() {
        try {
            // Wait for the specific text "Products" to appear
            waitForVisibility(productHeader);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
