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
    public void onTestFailure(ITestResult result) {
        WebDriver driver = DriverManager.getDriver();
        if (driver != null) {
            saveScreenshot(driver);
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        // Collect results for a high-level summary
        String message = "🚀 *Parallel Test Execution Complete!* \n" +
                "Suite: " + context.getSuite().getName() + "\n" +
                "✅ Passed: " + context.getPassedTests().size() + "\n" +
                "❌ Failed: " + context.getFailedTests().size() + "\n" +
                "⏩ Skipped: " + context.getSkippedTests().size();

        // Trigger the notification - sends notification to the team when the Docker run is done
        SlackUtils.sendResult(message);
    }

    @Attachment(value = "Page Screenshot", type = "image/png")
    public byte[] saveScreenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
