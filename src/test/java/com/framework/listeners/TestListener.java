package com.framework.listeners;

import com.framework.driver.DriverManager;
import com.framework.utils.SlackUtils;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("--------------------------------------------------");
        System.out.println("[INFO] STARTED TEST: " + result.getName());
        System.out.println("--------------------------------------------------");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("[INFO] PASSED: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.err.println("[ERROR] FAILED: " + result.getName());
        WebDriver driver = DriverManager.getDriver();
        if (driver != null) {
            System.out.println("[INFO] Capturing Screenshot...");
            saveScreenshot(driver);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("[WARN] SKIPPED: " + result.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        // Summary Message for Slack
        String message = "Execution Complete!\n" +
                "Suite: " + context.getSuite().getName() + "\n" +
                "Passed: " + context.getPassedTests().size() + "\n" +
                "Failed: " + context.getFailedTests().size() + "\n" +
                "Skipped: " + context.getSkippedTests().size();

        // Safe Slack Notification
        try {
            System.out.println("[INFO] Sending Slack Notification...");
            SlackUtils.sendResult(message);
        } catch (Exception e) {
            System.err.println("[WARN] Slack Notification Skipped: " + e.getMessage());
        }
    }

    @Attachment(value = "Page Screenshot", type = "image/png")
    public byte[] saveScreenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
