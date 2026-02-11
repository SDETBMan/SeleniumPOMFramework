package com.framework.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * AiHelper: Generates dynamic test data using OpenAI.
 * Demonstrates integration of LLMs for autonomous testing capabilities.
 */
public class AiHelper {

    private static final String API_KEY = System.getenv("OPENAI_API_KEY");
    private static final boolean USE_REAL_AI = (API_KEY != null && !API_KEY.isEmpty());

    /**
     * Generates test data based on a natural language prompt.
     * Priority: OpenAI API > Local Mock Data.
     */
    public static String generateTestData(String prompt) {
        if (!USE_REAL_AI) {
            return generateMockData(prompt);
        }

        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Payload: Instructs AI to return ONLY the requested string
            String jsonInput = String.format(
                    "{\"model\": \"gpt-3.5-turbo\", \"messages\": [" +
                            "{\"role\": \"system\", \"content\": \"You are a test data generator. Return ONLY the raw data string requested.\"}," +
                            "{\"role\": \"user\", \"content\": \"%s\"}], \"max_tokens\": 20}", prompt);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read and Parse Response using GSON
            Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8);
            String responseBody = scanner.useDelimiter("\\A").next();
            scanner.close();

            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            return json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString().trim();

        } catch (Exception e) {
            System.err.println("[ERROR] AI Data Generation Failed: " + e.getMessage());
            return generateMockData(prompt);
        }
    }

    /**
     * Fallback mock data to ensure test stability when API is unavailable.
     */
    private static String generateMockData(String prompt) {
        String lowerPrompt = prompt.toLowerCase();

        if (lowerPrompt.contains("email")) {
            return "test_user_" + System.currentTimeMillis() + "@example.com";
        }
        if (lowerPrompt.contains("password")) {
            return "SecurePass!" + (int) (Math.random() * 1000);
        }
        if (lowerPrompt.contains("username")) {
            return "QA_User_" + (int) (Math.random() * 1000);
        }

        return "Default_Test_Value";
    }
}
