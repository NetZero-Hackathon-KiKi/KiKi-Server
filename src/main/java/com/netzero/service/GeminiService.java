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

        String prompt = "너는 탄소중립 실천 미션 인증 사진을 검증하는 심사관이야.\n\n" +
                "미션: \"" + questDescription + "\"\n\n" +
                "판단 기준:\n" +
                "- 사진에 미션과 직접적으로 관련된 핵심 물체나 행동이 명확하게 보여야 true\n" +
                "- 미션과 관련 없는 사진(바닥, 천장, 풍경, 셀카, 음식 등)은 반드시 false\n" +
                "- 애매하거나 미션 수행을 확인할 수 없으면 false\n\n" +
                "예시:\n" +
                "- '텀블러 사용하기' → 텀블러나 재사용 컵이 사진에 명확히 보여야 true. 일회용 컵이나 빈 테이블은 false\n" +
                "- '분리수거하기' → 분리수거함에 재활용품을 넣는 모습이 보여야 true. 쓰레기통만 보이면 false\n" +
                "- '대중교통 이용하기' → 버스/지하철 내부나 교통카드 태그 장면이 보여야 true. 도로 사진은 false\n" +
                "- '자전거 이용하기' → 자전거가 명확히 보여야 true. 주차장이나 도로만 보이면 false\n\n" +
                "반드시 true 또는 false 중 하나만 답해. 다른 말은 절대 하지 마.";

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(
                                Map.of("text", prompt),
                                Map.of("inline_data", Map.of(
                                        "mime_type", mimeType != null ? mimeType : "image/jpeg",
                                        "data", base64Image
                                ))
                        )
                ))
        );

        String response = webClient.post()
                .uri("/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return response != null && response.toLowerCase().contains("true");
    }
}
