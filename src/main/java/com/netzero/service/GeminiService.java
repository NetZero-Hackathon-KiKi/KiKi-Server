package com.netzero.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api-key}")
    private String apiKey;

    public GeminiService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024))
                .build();
    }

    public boolean verifyQuestImage(byte[] imageBytes, String mimeType, String questDescription) {
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        String prompt = "You are a strict photo verification judge.\n\n" +
                "MISSION: \"" + questDescription + "\"\n\n" +
                "RULES:\n" +
                "1. The photo MUST clearly show the key object or action directly related to the mission.\n" +
                "2. Return ONLY the word PASS or FAIL. No other text.\n\n" +
                "FAIL examples (return FAIL for these):\n" +
                "- Floor, ceiling, wall, sky, random scenery\n" +
                "- Selfie without mission-related object\n" +
                "- Blurry or unrecognizable photo\n" +
                "- Food, desk, computer (unless mission-related)\n" +
                "- Any photo where the mission object is NOT visible\n\n" +
                "PASS examples (return PASS only for these):\n" +
                "- Mission is 'use tumbler' → tumbler/reusable cup clearly visible\n" +
                "- Mission is 'recycle' → recycling bin with items clearly visible\n" +
                "- Mission is 'use public transport' → inside bus/subway clearly visible\n" +
                "- Mission is 'use bicycle' → bicycle clearly visible\n\n" +
                "When in doubt, return FAIL.\n" +
                "Answer with PASS or FAIL only:";

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(
                                Map.of("text", prompt),
                                Map.of("inline_data", Map.of(
                                        "mime_type", mimeType != null ? mimeType : "image/jpeg",
                                        "data", base64Image
                                ))
                        )
                )),
                "generationConfig", Map.of(
                        "maxOutputTokens", 10,
                        "temperature", 0.1
                )
        );

        String response = webClient.post()
                .uri("/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractResult(response);
    }

    private boolean extractResult(String response) {
        if (response == null) return false;
        try {
            JsonNode root = objectMapper.readTree(response);
            String text = root.path("candidates").path(0)
                    .path("content").path("parts").path(0)
                    .path("text").asText().trim().toUpperCase();
            return text.contains("PASS");
        } catch (Exception e) {
            return false;
        }
    }
}
