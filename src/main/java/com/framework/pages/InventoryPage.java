package com.framework.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

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
        List<WebElement> badges = driver.findElements(cartBadge);
        return badges.isEmpty() ? 0 : Integer.parseInt(badges.get(0).getText());
    }

    public void waitForCartBadge() {
        waitForVisibility(cartBadge);
    }

    // ==================================================
    // 2. INTERACTION METHODS
    // ==================================================

    public void addToCart(String productName) {
        if (driver instanceof AndroidDriver || driver instanceof IOSDriver) {
            System.out.println("[WARN] Mobile AddToCart not yet implemented");
        } else {
            String formattedName = productName.toLowerCase().replace(" ", "-");
            By addButtonLocator = By.id("add-to-cart-" + formattedName);
            By removeButtonLocator = By.id("remove-" + formattedName);

            WebElement button = driver.findElement(addButtonLocator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);

            waitForVisibility(removeButtonLocator);
        }
    }

    public void removeFromCart(String productName) {
        // Updated XPath with a relative child selector (.) for better precision
        String xpath = "//div[text()='" + productName + "']/ancestor::div[@class='inventory_item_description']//button[text()='Remove']";

        // Lead-level: Wait for the specific dynamic element to appear
        waitForVisibility(By.xpath(xpath));

        WebElement removeButton = driver.findElement(By.xpath(xpath));
        removeButton.click();
        System.out.println("[LOG] Removed item from cart: " + productName);
    }

    public CartPage goToCart() {
        click(cartLink, "Shopping Cart Icon");
        return new CartPage(driver);
    }
}
