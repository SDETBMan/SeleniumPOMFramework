package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.driver.DriverManager;
import com.framework.pages.InventoryPage;
import com.framework.pages.LoginPage;
import com.framework.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AddToCartTest extends BaseTest {

    @Test(groups = {"regression"})
    public void testAddBackpackToCart() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        InventoryPage inventoryPage = new InventoryPage(DriverManager.getDriver());

        loginPage.login(ConfigReader.getProperty("app_username"), ConfigReader.getProperty("app_password"));

        // 1. Initial Check (Should be 0)
        Assert.assertEquals(inventoryPage.getCartItemCount(), 0, "Cart should be empty at start!");

        // 2. Add Item
        inventoryPage.addToCart("Sauce Labs Backpack");

        // 3. Wait for the UI to update!
        inventoryPage.waitForCartBadge();

        // 4. Validate Count (Should be 1)
        int currentCount = inventoryPage.getCartItemCount();
        System.out.println(">>> 🛒 Cart Count is now: " + currentCount);

        Assert.assertEquals(currentCount, 1, "Cart count did not update to 1!");
    }
}
