package com.framework.pages.mobile;

import com.framework.pages.BasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

/**
 * MobileLoginPage: Mapped specifically for the "Swag Labs" Mobile App.
 */
public class MobileLoginPage extends BasePage {

    WebDriver driver;

    // 1. Locators (Real Swag Labs IDs)

    // Username Field
    // Android & iOS use "test-" prefixes in this specific demo app for stability.
    @AndroidFindBy(accessibility = "test-Username")
    @iOSXCUITFindBy(accessibility = "test-Username")
    private WebElement usernameField;

    // Password Field
    @AndroidFindBy(accessibility = "test-Password")
    @iOSXCUITFindBy(accessibility = "test-Password")
    private WebElement passwordField;

    // Login Button
    @AndroidFindBy(accessibility = "test-LOGIN")
    @iOSXCUITFindBy(accessibility = "test-LOGIN")
    private WebElement loginBtn;

    // Error Message (When login fails)
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Username and password do not match')]")
    @iOSXCUITFindBy(accessibility = "test-Error message")
    private WebElement errorMsg;

    // Dashboard / Products Page (Verification Element)
    // On Android, the header usually says "PRODUCTS" or shows a cart icon.
    // We use the "Cart" icon as a sure sign we are logged in.
    @AndroidFindBy(accessibility = "test-Cart")
    @iOSXCUITFindBy(accessibility = "test-Cart")
    private WebElement dashboardCartIcon;

    // 2. Constructor
    public MobileLoginPage(WebDriver driver) {
        super(driver);
    }

    // 3. Actions
    public void enterUsername(String user) {
        usernameField.clear(); // Good practice to clear before typing!
        usernameField.sendKeys(user);
    }

    public void enterPassword(String pass) {
        passwordField.clear();
        passwordField.sendKeys(pass);
    }

    public void tapLoginButton() {
        loginBtn.click();
    }

    public String getErrorMessage() {
        return errorMsg.getText();
    }

    public boolean isDashboardVisible() {
        try {
            return dashboardCartIcon.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
            public boolean isErrorMessageDisplayed () {
                return errorMsg.isDisplayed();
            }
        }

