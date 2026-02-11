package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.driver.DriverManager;
import com.framework.pages.CartPage;
import com.framework.pages.InventoryPage;
import com.framework.pages.LoginPage;
import com.framework.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AddToCartTest extends BaseTest {

    // ---------------------------------------------------------
    // HAPPY PATH (Smoke): Critical User Flow
    // ---------------------------------------------------------
    @Test(groups = {"smoke", "regression", "web"})
    public void testAddBackpackToCart() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        InventoryPage inventoryPage = new InventoryPage(DriverManager.getDriver());

        // Step 1: Login
        loginPage.login(ConfigReader.getProperty("app_username"), ConfigReader.getProperty("app_password"));

        // Step 2: Pre-Condition Check
        Assert.assertEquals(inventoryPage.getCartItemCount(), 0, "Pre-condition Failed: Cart was not empty.");

        // Step 3: Action - Add 1 Item
        inventoryPage.addToCart("Sauce Labs Backpack");

        // Step 4: UI Sync
        inventoryPage.waitForCartBadge();
        Assert.assertEquals(inventoryPage.getCartItemCount(), 1, "Inventory page badge did not update.");

        // Step 5: Navigation - Move to Cart Page
        // This is where we use the method we just fixed!
        CartPage cartPage = inventoryPage.goToCart();

        // Step 6: Final Validation - Verify item is actually in the cart list
        Assert.assertTrue(cartPage.isItemInCart("Sauce Labs Backpack"),
                "VALIDATION FAILED: Item added but not found on Cart Page!");

        System.out.println("[PASS] Backpack successfully added and verified in cart.");
    }

    // ---------------------------------------------------------
    // EDGE CASE (Regression): Boundary Testing
    // Does the system handle multiple rapid additions correctly?
    // ---------------------------------------------------------
    @Test(groups = {"regression", "web"})
    public void testAddMultipleItemsToCart() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        InventoryPage inventoryPage = new InventoryPage(DriverManager.getDriver());

        loginPage.login(ConfigReader.getProperty("app_username"), ConfigReader.getProperty("app_password"));

        // Action: Add 3 specific items
        inventoryPage.addToCart("Sauce Labs Backpack");
        inventoryPage.addToCart("Sauce Labs Bike Light");
        inventoryPage.addToCart("Sauce Labs Bolt T-Shirt");

        // UI Sync
        inventoryPage.waitForCartBadge();

        // Validation: Expect 3
        int currentCount = inventoryPage.getCartItemCount();
        Assert.assertEquals(currentCount, 3, "Cart count failed to reach 3.");
    }

    // ---------------------------------------------------------
    // NEGATIVE / STATE TEST (Regression): Undo Action
    // Can the user correct a mistake (Remove item)?
    // ---------------------------------------------------------
    @Test(groups = {"regression", "web"})
    public void testAddAndRemoveItem() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        InventoryPage inventoryPage = new InventoryPage(DriverManager.getDriver());

        loginPage.login(ConfigReader.getProperty("app_username"), ConfigReader.getProperty("app_password"));

        // Step 1: Add Item
        inventoryPage.addToCart("Sauce Labs Fleece Jacket");
        Assert.assertEquals(inventoryPage.getCartItemCount(), 1, "Failed to add item initially.");

        // Step 2: Remove Item (You might need to add this method to InventoryPage!)
        inventoryPage.removeFromCart("Sauce Labs Fleece Jacket");

        // Step 3: Verify Cart is Empty again
        Assert.assertEquals(inventoryPage.getCartItemCount(), 0, "Cart count did not return to 0 after removal.");
    }
}
