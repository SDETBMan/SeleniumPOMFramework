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

    // 1. VISIBILITY: Log when a test starts so you can track progress
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("========================================");
        System.out.println(">>> 🚀 STARTED TEST: " + result.getName());
        System.out.println("========================================");
    }

    // 2. VISIBILITY: Log when a test passes
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println(">>> ✅ PASSED: " + result.getName());
    }

    // 3. CAPTURE: Log failure and take screenshot
    @Override
    public void onTestFailure(ITestResult result) {
        System.err.println(">>> ❌ FAILED: " + result.getName());

        WebDriver driver = DriverManager.getDriver();
        if (driver != null) {
            System.out.println(">>> 📸 Taking Screenshot...");
            saveScreenshot(driver);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println(">>> ⏭ SKIPPED: " + result.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        // Collect results for a high-level summary
        String message = "🚀 *Parallel Test Execution Complete!* \n" +
                "Suite: " + context.getSuite().getName() + "\n" +
                "✅ Passed: " + context.getPassedTests().size() + "\n" +
                "❌ Failed: " + context.getFailedTests().size() + "\n" +
                "⏩ Skipped: " + context.getSkippedTests().size();

        // 4. ROBUSTNESS: Wrap Slack notification to prevent crashes if config is missing
        try {
            System.out.println(">>> 🔔 Sending Slack Notification...");
            SlackUtils.sendResult(message);
        } catch (Exception e) {
            System.err.println(">>> ⚠️ Slack Notification Failed (Check Config): " + e.getMessage());
        }
    }

    // 5. ALLURE ATTACHMENT
    @Attachment(value = "Page Screenshot", type = "image/png")
    public byte[] saveScreenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
