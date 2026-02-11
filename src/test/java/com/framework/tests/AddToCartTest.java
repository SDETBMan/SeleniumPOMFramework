package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.driver.DriverManager;
import com.framework.pages.InventoryPage;
import com.framework.pages.LoginPage;
import com.framework.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AddToCartTest: Validates the core e-commerce workflow of selecting and adding items.
 */
public class AddToCartTest extends BaseTest {

    @Test(groups = {"regression", "smoke"})
    public void testAddBackpackToCart() {
        // Initialize Pages
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        InventoryPage inventoryPage = new InventoryPage(DriverManager.getDriver());

        // Step 1: Login
        loginPage.login(ConfigReader.getProperty("app_username"), ConfigReader.getProperty("app_password"));

        // Step 2: Verify initial state
        int initialCount = inventoryPage.getCartItemCount();
        System.out.println("[INFO] Initial Cart Count: " + initialCount);
        Assert.assertEquals(initialCount, 0, "Pre-condition Failed: Cart was not empty at start.");

        // Step 3: Add "Sauce Labs Backpack" to cart
        inventoryPage.addToCart("Sauce Labs Backpack");

        // Step 4: Synchronize with UI
        inventoryPage.waitForCartBadge();

        // Step 5: Final Validation
        int currentCount = inventoryPage.getCartItemCount();
        System.out.println("[INFO] Updated Cart Count: " + currentCount);
        Assert.assertEquals(currentCount, 1, "Validation Failed: Cart count did not increment to 1.");
    }
}
