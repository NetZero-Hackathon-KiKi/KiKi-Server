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
                "당신은 환경 미션 사진 인증 시스템입니다.",
                "",
                "미션 제목: \"" + questTitle + "\"",
                "미션 설명: \"" + questDescription + "\"",
                "",
                "사진을 보고, 이 미션과 관련된 사진인지 판단하세요.",
                "관대하게 판단하세요 - 미션 주제와 조금이라도 관련이 있으면 TRUE로 판단합니다.",
                "미션의 핵심 물건이 사진에 보이면 무조건 TRUE입니다.",
                "",
                "판단 기준:",
                "- '텀블러사용하기' 미션: 텀블러, 보온병, 리유저블컵, 머그컵, 개인컵이 사진에 보이면 → TRUE",
                "- '분리수거하기' 미션: 분리수거함, 재활용 쓰레기, 분리된 쓰레기가 보이면 → TRUE",
                "- '안쓰는 멀티탭 뽑기' 미션: 멀티탭, 콘센트, 전원 관련 사진이면 → TRUE",
                "- '음식물 남기지 않기' 미션: 깨끗한 식판, 빈 그릇, 식사 관련 사진이면 → TRUE",
                "- '대중교통 이용하기' 미션: 버스, 지하철, 교통카드 관련 사진이면 → TRUE",
                "- '캠퍼스 플로깅' 미션: 쓰레기 줍기, 쓰레기봉투, 야외 청소 사진이면 → TRUE",
                "- '자전거 출퇴근' 미션: 자전거가 보이면 → TRUE",
                "- 미션과 전혀 관련 없는 사진 (예: 텀블러 미션에 고양이 사진) → FALSE",
                "",
                "반드시 TRUE 또는 FALSE 중 하나만 답하세요."
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
