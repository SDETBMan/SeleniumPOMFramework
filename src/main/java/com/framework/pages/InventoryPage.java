package com.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class InventoryPage extends BasePage {

    private final By productsHeader = By.className("title");
    private final By cartBadge = By.className("shopping_cart_badge");
    private final By cartLink = By.className("shopping_cart_link");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

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

    /**
     * JS Click implementation for reliability.
     * Waits for the button state to change (Add -> Remove) to confirm success.
     */
    public void addToCart(String productName) {
        String formattedName = productName.toLowerCase().replace(" ", "-");
        By addButtonLocator = By.id("add-to-cart-" + formattedName);
        By removeButtonLocator = By.id("remove-" + formattedName);

        // 1. Find and Click (JS Force)
        WebElement button = driver.findElement(addButtonLocator);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", button);

        // 2. Synchronization: Wait for "Remove" button to appear
        waitForVisibility(removeButtonLocator);
    }

    public void goToCart() {
        click(cartLink, "Shopping Cart Icon");
    }
}
