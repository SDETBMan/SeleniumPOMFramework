package com.framework.utils;

import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class AiHelper {

    // Toggle this to TRUE if we actually have an OpenAI Key
    private static final boolean USE_REAL_AI = false;
    private static final String API_KEY = "YOUR_OPENAI_KEY_HERE";

    /**
     * Asks AI to generate a test string based on a prompt.
     * Example: "Generate a valid username for a banking app"
     */
    public static String generateTestData(String prompt) {
        if (!USE_REAL_AI) {
            return mockAiResponse(prompt);
        }

        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Create the JSON Request
            String jsonInput = "{"
                    + "\"model\": \"gpt-3.5-turbo\","
                    + "\"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}],"
                    + "\"max_tokens\": 50"
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read Response
            Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8);
            String responseBody = scanner.useDelimiter("\\A").next();
            scanner.close();

            // Parse JSON to get just the text
            JSONObject json = new JSONObject(responseBody);
            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content").trim();

        } catch (Exception e) {
            System.err.println("AI Request Failed: " + e.getMessage());
            return "Fallback_Test_Data";
        }
    }

    // --- MOCK MODE (For Portfolio Stability) ---
    private static String mockAiResponse(String prompt) {
        System.out.println(">>> AI PROMPT RECEIVED: " + prompt);

        if (prompt.contains("email")) return "ai_generated_user_" + System.currentTimeMillis() + "@test.com";
        if (prompt.contains("password")) return "ComplexPass!23";
        if (prompt.contains("error")) return "Invalid credentials provided.";

        return "Standard_Mock_Data";
    }
}
