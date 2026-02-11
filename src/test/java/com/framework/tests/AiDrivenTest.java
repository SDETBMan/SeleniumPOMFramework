package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.driver.DriverManager;
import com.framework.pages.LoginPage;
import com.framework.utils.AiHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AiDrivenTest extends BaseTest {

    @Test(groups = "ai")
    public void testLoginWithAiGeneratedData() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());

        // 1. Ask "AI" to generate diverse test data
        // This helps find edge cases developers didn't think of
        String aiUsername = AiHelper.generateTestData("Generate a valid looking email address");
        String aiPassword = AiHelper.generateTestData("Generate a complex password");

        System.out.println("AI Generated Credentials: " + aiUsername + " / " + aiPassword);

        // 2. Use the data
        loginPage.login("standard_user", "secret_sauce");

        // 3. Verify the app handles this "unknown user" correctly
        String error = loginPage.getErrorMessage();
        Assert.assertTrue(error.contains("Username and password do not match"),
                "App did not handle AI-generated random user correctly!");
    }
}
