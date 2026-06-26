package com.netzero.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api-key}")
    private String apiKey;

    public GeminiService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    public boolean verifyQuestImage(String imageUrl, String questDescription) {
        String prompt = "이 이미지가 다음 미션을 수행한 증거 사진인지 판단해줘. " +
                "미션: \"" + questDescription + "\". " +
                "미션과 일치하면 true, 아니면 false만 답해줘.";

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(
                                Map.of("text", prompt),
                                Map.of("inline_data", Map.of(
                                        "mime_type", "image/jpeg",
                                        "data", imageUrl
                                ))
                        )
                ))
        );

        String response = webClient.post()
                .uri("/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return response != null && response.toLowerCase().contains("true");
    }
}
