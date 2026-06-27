package com.netzero.service;

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
    @Value("${gemini.api-key}")
    private String apiKey;

    public GeminiService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024))
                .build();
    }

    public boolean verifyQuestImage(byte[] imageBytes, String mimeType, String questTitle, String questDescription) {
        log.info("[Gemini] 미션 인증 요청 - 미션: {}, 이미지크기: {}bytes", questTitle, imageBytes.length);

        try {
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            String prompt = "이 사진이 \"" + questTitle + "\" 미션과 관련이 있으면 TRUE, 완전히 무관하면 FALSE를 출력해. 한 단어만 답해.";

            Map<String, Object> body = Map.of(
                    "contents", List.of(Map.of(
                            "parts", List.of(
                                    Map.of("text", prompt),
                                    Map.of("inlineData", Map.of(
                                            "mimeType", mimeType != null ? mimeType : "image/jpeg",
                                            "data", base64Image
                                    ))
                            )
                    )),
                    "generationConfig", Map.of(
                            "maxOutputTokens", 10,
                            "temperature", 0.0
                    )
            );

            String response = webClient.post()
                    .uri("/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("[Gemini] API 응답: {}", response);

            if (response != null && response.toUpperCase().contains("FALSE")) {
                log.info("[Gemini] 판정 결과: FAILED");
                return false;
            }

            log.info("[Gemini] 판정 결과: SUCCESS");
            return true;
        } catch (Exception e) {
            log.error("[Gemini] API 호출 실패, 자동 승인: {}", e.getMessage());
            return true;
        }
    }

}
