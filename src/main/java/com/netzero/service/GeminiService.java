package com.netzero.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;

    public GeminiService(@Value("${gemini.api-key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .defaultHeader("x-goog-api-key", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public boolean verifyQuestImage(String imageUrl, String questTitle, String questDescription) {
        String prompt = String.format(
                "당신은 퀘스트 인증 심사관입니다. 아래 퀘스트를 수행했는지 사진으로 판별해주세요.\n\n" +
                "퀘스트 제목: %s\n퀘스트 설명: %s\n\n" +
                "이 사진이 해당 퀘스트를 수행한 증거로 적합하면 'PASS', 아니면 'FAIL'만 답하세요.",
                questTitle, questDescription);

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(
                                Map.of("text", prompt),
                                Map.of("inlineData", Map.of(
                                        "mimeType", "image/jpeg",
                                        "data", downloadImageAsBase64(imageUrl)
                                ))
                        )
                ))
        );

        Map response = webClient.post()
                .uri("/models/gemini-flash-latest:generateContent")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        String result = extractText(response);
        return result.toUpperCase().contains("PASS");
    }

    private String downloadImageAsBase64(String imageUrl) {
        byte[] imageBytes = WebClient.create().get()
                .uri(imageUrl)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map response) {
        List<Map> candidates = (List<Map>) response.get("candidates");
        Map content = (Map) candidates.get(0).get("content");
        List<Map> parts = (List<Map>) content.get("parts");
        return (String) parts.get(0).get("text");
    }
}
