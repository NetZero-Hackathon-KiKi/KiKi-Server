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

        String prompt = "사진이 \"" + questTitle + "\" 미션 인증으로 적합한지 판단해.\n" +
                "미션 설명: " + questDescription + "\n\n" +
                "규칙:\n" +
                "- 사진에 미션의 핵심 물체나 행동이 보이면 TRUE\n" +
                "- 바닥, 천장, 벽, 관련 없는 사물이면 FALSE\n" +
                "- 애매하면 FALSE\n\n" +
                "TRUE 또는 FALSE 한 단어만 답해.";

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
                .uri("/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return parseResult(response);
    }

    private boolean parseResult(String response) {
        if (response == null) return false;
        try {
            JsonNode root = objectMapper.readTree(response);
            String text = root.path("candidates").path(0)
                    .path("content").path("parts").path(0)
                    .path("text").asText().trim().toUpperCase();
            return text.contains("TRUE");
        } catch (Exception e) {
            return false;
        }
    }
}
