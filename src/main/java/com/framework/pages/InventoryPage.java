package com.framework.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * InventoryPage: Manages product selection and cart interactions for Web and Mobile.
 */
public class InventoryPage extends BasePage {

    private By productsHeader;
    private By cartBadge;
    private By cartLink;

    public InventoryPage(WebDriver driver) {
        super(driver);
        initLocators();
    }

    private void initLocators() {
        boolean isNativeMobile = (driver instanceof AndroidDriver) || (driver instanceof IOSDriver);

        if (isNativeMobile) {
            if (driver instanceof AndroidDriver) {
                productsHeader = By.xpath("//android.widget.TextView[@text='PRODUCTS']");
            } else {
                productsHeader = AppiumBy.accessibilityId("PRODUCTS");
            }
            cartBadge = AppiumBy.accessibilityId("test-Cart badge");
            cartLink = AppiumBy.accessibilityId("test-Cart");
        } else {
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
        // Now using inherited safe check and text retrieval
        if (isElementDisplayed(cartBadge)) {
            String countText = getText(cartBadge);
            return countText.isEmpty() ? 0 : Integer.parseInt(countText);
        }
        return 0;
    }

    public void waitForCartBadge() {
        waitForVisibility(cartBadge);
    }

    // ==================================================
    // 2. INTERACTION METHODS
    // ==================================================

    public void addToCart(String productName) {
        String formattedName = productName.toLowerCase().replace(" ", "-");
        By addButton = By.id("add-to-cart-" + formattedName);
        By removeButton = By.id("remove-" + formattedName);

        // 1. Wait and capture the element in one go
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(addButton));

        // 2. Log the action (Optional, but great for Allure reports)
        System.out.println("[WEB-ACTION] Force Clicking Add to Cart: " + productName);

        // 3. Force the click via JS to handle Headless/CI latency
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);

        // 4. Confirm the state change (The 'Remove' button appearing)
        waitForVisibility(removeButton);
    }

    public void removeFromCart(String productName) {
        String xpath = "//div[text()='" + productName + "']/ancestor::div[@class='inventory_item_description']//button[text()='Remove']";
        click(By.xpath(xpath), "Remove Button");

        // State-sync: waits for the badge to actually disappear
        waitForTextToBePresent(cartBadge, "");
    }

    public CartPage goToCart() {
        click(cartLink, "Shopping Cart Icon");
        // Ensure we are actually on the cart page before proceeding
        waitForUrlToContain("cart.html");
        return new CartPage(driver);
    }
}
