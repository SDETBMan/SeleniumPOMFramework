package com.framework.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class InventoryPage extends BasePage {

    // Locators are now non-final so we can set them in the Constructor
    private By productsHeader;
    private By cartBadge;
    private By cartLink;

    public InventoryPage(WebDriver driver) {
        super(driver);
        initLocators();
    }

    /**
     *
     * Initialize locators based on the active Driver (Platform).
     */
    private void initLocators() {
        if (driver instanceof AndroidDriver) {
            // ANDROID NATIVE LOCATORS
            productsHeader = By.xpath("//android.widget.TextView[@text='PRODUCTS']");
            cartBadge      = By.xpath("//android.widget.TextView[@content-desc='test-Cart badge']");
            cartLink       = By.xpath("//android.view.ViewGroup[@content-desc='test-Cart']");
        }
        else if (driver instanceof IOSDriver) {
            // iOS NATIVE LOCATORS
            productsHeader = By.id("PRODUCTS");
            cartBadge      = By.id("test-Cart badge");
            cartLink       = By.id("test-Cart");
        }
        else {
            // WEB LOCATORS (Default)
            productsHeader = By.className("title");
            cartBadge      = By.className("shopping_cart_badge");
            cartLink       = By.className("shopping_cart_link");
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
            // Mobile Interaction (Native App)
            // Note: In a real app, we would need specific Accessibility IDs for each product
            // For now, we are focusing on Login, so we skip the implementation details for mobile add-to-cart
            System.out.println(">>> Mobile AddToCart not yet implemented");
        } else {
            // Web Interaction (JS Click)
            String formattedName = productName.toLowerCase().replace(" ", "-");
            By addButtonLocator = By.id("add-to-cart-" + formattedName);
            By removeButtonLocator = By.id("remove-" + formattedName);

            WebElement button = driver.findElement(addButtonLocator);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", button);
            waitForVisibility(removeButtonLocator);
        }
    }

    public void goToCart() {
        click(cartLink, "Shopping Cart Icon");
    }
}
