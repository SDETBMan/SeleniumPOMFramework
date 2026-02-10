package com.framework.utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.io.File;

public class BrowserStackUtils {

    private static final String UPLOAD_URL = "https://api-cloud.browserstack.com/app-automate/upload";

    public static void main(String[] args) {

        // SKIP SWITCH: Allows bypassing upload for local runs
        if (System.getProperty("skipAppUpload") != null) {
            System.out.println("⏩ SKIPPING APP UPLOAD (Requested via -DskipAppUpload)...");
            System.out.println("   Using previously uploaded apps on BrowserStack.");
            return;
        }
        // Define file paths
        String androidPath = "src/test/resources/apps/Android.SauceLabs.Mobile.Sample.app.2.7.1.apk";
        String iosPath = "src/test/resources/apps/iOS.RealDevice.SauceLabs.Mobile.Sample.app.2.7.1.ipa";

        System.out.println(">>> STARTING DUAL APP UPLOAD <<<");

        // 1. Get and Sanitize Credentials
        String user = getUsername();
        String key = getAccessKey();

        if (user == null || key == null) {
            System.err.println("FATAL: Credentials missing. Check Env Vars or Config.");
            return;
        }

        System.out.println("DEBUG: User: " + user.substring(0, 3) + "***");
        System.out.println("DEBUG: Key Length: " + key.length()); // Good sanity check

        // 2. Upload Apps
        uploadAppIfExists(androidPath, "QA_Director_Android_Build", user, key);
        uploadAppIfExists(iosPath, "QA_Director_iOS_Build", user, key);

        System.out.println(">>> UPLOAD PROCESS COMPLETE <<<");
    }

    public static void uploadAppIfExists(String filePath, String customId, String user, String key) {
        File appFile = new File(filePath);
        if (!appFile.exists()) {
            System.err.println("SKIP: File not found: " + filePath);
            return;
        }

        System.out.println("Uploading " + appFile.getName() + " as [" + customId + "]...");

        try {
            Response response = RestAssured.given()
                    // Use PREEMPTIVE Auth (Sends creds immediately)
                    .auth().preemptive().basic(user, key)
                    .header("Content-Type", "multipart/form-data")
                    .multiPart("file", appFile)
                    .multiPart("custom_id", customId)
                    .post(UPLOAD_URL);

            if (response.getStatusCode() == 200) {
                String appUrl = response.jsonPath().getString("app_url");
                System.out.println("SUCCESS! App URL: " + appUrl);
            } else {
                System.err.println("FAILED: " + response.getStatusCode());
                System.err.println("   Response: " + response.getBody().asString());
            }
        } catch (Exception e) {
            System.err.println("EXCEPTION: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- ROBUST CREDENTIAL GETTERS (Now with .trim()) ---
    private static String getUsername() {
        String env = System.getenv("BROWSERSTACK_USERNAME");
        String val = (env != null && !env.isEmpty()) ? env : ConfigReader.getProperty("cloud_username");
        return (val != null) ? val.trim() : null; // Remove hidden spaces
    }

    private static String getAccessKey() {
        String env = System.getenv("BROWSERSTACK_ACCESS_KEY");
        String val = (env != null && !env.isEmpty()) ? env : ConfigReader.getProperty("cloud_key");
        return (val != null) ? val.trim() : null; // Remove hidden spaces
    }

    // Compatibility method for older calls
    public static void uploadApp(String path) {
        uploadAppIfExists(path, "QA_Director_iOS_Build", getUsername(), getAccessKey());
    }
}

