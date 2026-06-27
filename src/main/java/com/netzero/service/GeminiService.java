package com.netzero.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
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

        String prompt = String.join("\n",
                "You are a mission photo verification system.",
                "",
                "Mission: \"" + questTitle + "\"",
                "Description: \"" + questDescription + "\"",
                "",
                "Look at the photo. Does it show reasonable evidence of this mission?",
                "Be lenient - if the photo is somewhat related to the mission topic, accept it.",
                "",
                "Examples:",
                "- Mission about tumbler/reusable cup: photo shows any tumbler, reusable cup, or thermos → TRUE",
                "- Mission about bicycle: photo shows any bicycle → TRUE",
                "- Mission about trash/cleanup: photo shows trash bags or picking up litter → TRUE",
                "- Completely unrelated photo (e.g. a cat photo for a tumbler mission) → FALSE",
                "",
                "Answer ONLY: TRUE or FALSE"
        );

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
                        "temperature", 0.0
                )
        );

        try {
            log.info("[Gemini] 미션 인증 요청 - 미션: {}, 이미지크기: {}bytes", questTitle, imageBytes.length);

            String response = webClient.post()
                    .uri("/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("[Gemini] API 응답: {}", response);

            boolean result = parseResult(response);
            log.info("[Gemini] 판정 결과: {}", result ? "SUCCESS" : "FAILED");
            return result;
        } catch (Exception e) {
            log.error("[Gemini] API 호출 실패: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean parseResult(String response) {
        if (response == null) {
            log.warn("[Gemini] 응답이 null");
            return false;
        }
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode parts = root.path("candidates").path(0)
                    .path("content").path("parts");

            if (parts.isMissingNode() || parts.isEmpty()) {
                log.warn("[Gemini] 응답에 candidates/parts 없음: {}", response);
                return false;
            }

            for (JsonNode part : parts) {
                String text = part.path("text").asText().trim().toUpperCase();
                log.info("[Gemini] 응답 텍스트: '{}'", text);
                if (text.contains("TRUE")) return true;
            }
            return false;
        } catch (Exception e) {
            log.error("[Gemini] 응답 파싱 실패: {}", e.getMessage());
            return false;
        }
    }
}
