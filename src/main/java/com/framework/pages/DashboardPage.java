package com.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DashboardPage extends BasePage {

    // 1. LOCATORS (Swag Labs Specific)
    // ==========================================

    @FindBy(className = "shopping_cart_link")
    private WebElement cartIcon;

    @FindBy(id = "react-burger-menu-btn")
    private WebElement menuButton;

    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;

    @FindBy(className = "title") // The header that says "Products"
    private WebElement pageTitle;


    // 2. CONSTRUCTOR
    // ==========================================
    public DashboardPage(WebDriver driver) {
        super(driver);
    }


    // 3. ACTION METHODS (Called by DashboardTest)
    // ==========================================

    public boolean isCartIconDisplayed() {
        try {
            return cartIcon.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickMenuButton() {
        // Open the hamburger menu
        menuButton.click();
    }

    public void clickLogoutButton() {
        // The logout link is inside the menu, so we must wait for the menu to slide in
        wait.until(ExpectedConditions.visibilityOf(logoutLink));
        logoutLink.click();
    }

    public String getWelcomeMessageText() {
        // Used to verify we are on the dashboard (e.g., checks for "Products" text)
        return pageTitle.getText();
    }
}
