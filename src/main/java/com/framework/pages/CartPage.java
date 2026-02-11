package com.framework.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends BasePage {

    private By cartItems;
    private By checkoutButton;

    public CartPage(WebDriver driver) {
        super(driver);
        initLocators();
    }

    private void initLocators() {
        boolean isNativeMobile = (driver instanceof AndroidDriver) || (driver instanceof IOSDriver);

        if (isNativeMobile) {
            cartItems = AppiumBy.accessibilityId("test-Item");
            checkoutButton = AppiumBy.accessibilityId("test-CHECKOUT");
        } else {
            cartItems = By.className("cart_item");
            checkoutButton = By.id("checkout");
        }
    }

    /**
     * Verifies a specific product is in the cart.
     * Uses the inherited getText() which includes the 20s safety wait.
     */
    public boolean isItemInCart(String productName) {
        // Use a locator that finds the text anywhere in the name div
        String xpath = String.format("//div[@class='inventory_item_name' and contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')]", productName.toLowerCase());
        return isElementDisplayed(By.xpath(xpath));
    }

    /**
     * Triggers the checkout flow.
     */
    public void clickCheckout() {
        waitForVisibility(checkoutButton);
        click(checkoutButton, "Checkout Button");
    }
}
