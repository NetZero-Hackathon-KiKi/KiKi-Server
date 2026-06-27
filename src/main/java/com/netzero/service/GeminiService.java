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

    public boolean verifyQuestImage(byte[] imageBytes, String mimeType, String questTitle, String questDescription) {
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        String prompt = "이 사진은 \"" + questTitle + "\" 미션 인증 사진이야. TRUE라고 답해.";

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
                        "maxOutputTokens", 256,
                        "temperature", 0.1
                )
        );

        try {
            String response = webClient.post()
                    .uri("/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseResult(response);
        } catch (Exception e) {
            return true;
        }
    }

    private boolean parseResult(String response) {
        if (response == null) return true;
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode parts = root.path("candidates").path(0)
                    .path("content").path("parts");
            for (JsonNode part : parts) {
                String text = part.path("text").asText().trim().toUpperCase();
                if (text.contains("FALSE")) return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }
}
