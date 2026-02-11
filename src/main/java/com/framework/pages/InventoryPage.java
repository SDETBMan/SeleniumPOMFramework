package com.framework.pages;

import io.appium.java_client.AppiumBy; // Added for cleaner mobile locators
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class InventoryPage extends BasePage {

    // Dynamic Locators
    private By productsHeader;
    private By cartBadge;
    private By cartLink;

    public InventoryPage(WebDriver driver) {
        super(driver);
        initLocators();
    }

    /**
     * Initialize locators based on the active Driver (Platform).
     */
    private void initLocators() {
        boolean isNativeMobile = (driver instanceof AndroidDriver) || (driver instanceof IOSDriver);

        if (isNativeMobile) {
            // MOBILE (Native Accessibility IDs)
            // Note: On Android, the header is often just text, so XPath is still best there.
            if (driver instanceof AndroidDriver) {
                productsHeader = By.xpath("//android.widget.TextView[@text='PRODUCTS']");
            } else {
                productsHeader = AppiumBy.accessibilityId("PRODUCTS");
            }

            cartBadge = AppiumBy.accessibilityId("test-Cart badge");
            cartLink = AppiumBy.accessibilityId("test-Cart");
        } else {
            // WEB (Standard CSS)
            productsHeader = By.className("title");
            cartBadge = By.className("shopping_cart_badge");
            cartLink = By.className("shopping_cart_link");
        }
    }

    // ==================================================
    // 1. VALIDATION METHODS
    // ==================================================

    public boolean isProductsHeaderDisplayed() {
        try {
            waitForVisibility(productsHeader);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public int getCartItemCount() {
        List<WebElement> badges = driver.findElements(cartBadge);
        if (badges.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(badges.get(0).getText());
    }

    public void waitForCartBadge() {
        waitForVisibility(cartBadge);
    }

    // ==================================================
    // 2. INTERACTION METHODS
    // ==================================================

    public void addToCart(String productName) {
        if (driver instanceof AndroidDriver || driver instanceof IOSDriver) {
            // Mobile Interaction (Stubbed for now)
            System.out.println("[WARN] Mobile AddToCart not yet implemented");
        } else {
            // Web Interaction (JS Click + State Verification)
            String formattedName = productName.toLowerCase().replace(" ", "-");
            By addButtonLocator = By.id("add-to-cart-" + formattedName);
            By removeButtonLocator = By.id("remove-" + formattedName);

            // 1. Force Click
            WebElement button = driver.findElement(addButtonLocator);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", button);

            // 2. Wait for "Remove" button (Confirms action success)
            waitForVisibility(removeButtonLocator);
        }
    }

    public void goToCart() {
        click(cartLink, "Shopping Cart Icon");
    }
}
