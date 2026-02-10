package com.framework.utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SlackUtils {
    // Replace with actual Webhook URL from Slack, Teams, etc
    private static final String WEBHOOK_URL = "https://hooks.slack.com/services/YOUR/WEBHOOK/PATH";

    public static void sendResult(String message) {
        try {
            URL url = new URL(WEBHOOK_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String jsonPayload = "{\"text\": \"" + message + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            }
            int responseCode = conn.getResponseCode();
            System.out.println("Notification sent. Status Code: " + responseCode);
        } catch (Exception e) {
            System.err.println("Notification failed: " + e.getMessage());
        }
    }
}
