package com.framework.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InventoryPage extends BasePage {

    // We will verify the page loaded by checking for the Cart Icon
    // This is more stable across platforms than text headers.
    private By cartIcon;

    public InventoryPage(WebDriver driver) {
        super(driver);

        if (driver instanceof AndroidDriver || driver instanceof IOSDriver) {
            // Both iOS and Android apps use this ID
            cartIcon = AppiumBy.accessibilityId("test-Cart");
        } else {
            // Web
            cartIcon = By.className("shopping_cart_link");
        }
    }

    public boolean isProductsHeaderDisplayed() {
        try {
            // Renamed logic, kept method name for compatibility with tests
            return driver.findElement(cartIcon).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
