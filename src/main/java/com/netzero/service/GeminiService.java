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

        String prompt = "너는 환경 보호 앱의 미션 사진 인증 판정관이야.\n\n" +
                "## 현재 미션\n" +
                "제목: " + questTitle + "\n" +
                "설명: " + questDescription + "\n\n" +
                "## 판정 규칙\n" +
                "- 사진에서 미션과 관련된 물체나 장면이 조금이라도 보이면 반드시 TRUE를 출력해.\n" +
                "- 사진의 품질, 각도, 밝기, 배경은 전혀 상관없어. 핵심 물체만 보이면 돼.\n" +
                "- 애매하면 TRUE로 판정해. 최대한 관대하게 판단해.\n" +
                "- 미션과 완전히 무관한 사진(예: 텀블러 미션에 동물 사진)만 FALSE야.\n\n" +
                "## 미션별 인정 물체 목록\n" +
                "텀블러사용하기 → 텀블러, 보온병, 머그컵, 텀블러컵, 리유저블컵, 개인컵, 스텐컵, 보냉병, 물병, 커피컵(일회용 아닌 것), 스타벅스 리유저블컵, 손에 들고 있는 컵\n" +
                "분리수거하기 → 분리수거함, 재활용 쓰레기, 페트병, 캔, 종이, 유리병, 분리수거장\n" +
                "안쓰는 멀티탭 뽑기 → 멀티탭, 콘센트, 플러그, 전원 스위치, 전선, 어댑터\n" +
                "음식물 남기지 않기 → 빈 그릇, 깨끗한 접시, 식판, 다 먹은 음식, 식당, 밥\n" +
                "대중교통 이용하기 → 버스, 지하철, 전철, 기차, 택시, 교통카드, 버스정류장, 지하철역, 좌석\n" +
                "캠퍼스 플로깅 → 쓰레기봉투, 쓰레기 줍기, 집게, 장갑, 야외 청소\n" +
                "에너지 절약 캠페인 → 포스터, 캠페인 현수막, 행사, 에너지 절약 관련 장면\n" +
                "자전거 출퇴근 → 자전거, 킥보드, 헬멧, 자전거 도로\n\n" +
                "TRUE 또는 FALSE 한 단어만 출력해.";

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

        try {
            log.info("[Gemini] 미션 인증 요청 - 미션: {}, 이미지크기: {}bytes", questTitle, imageBytes.length);

            String response = webClient.post()
                    .uri("/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("[Gemini] API 응답: {}", response);

            boolean result = parseResult(response);
            log.info("[Gemini] 판정 결과: {}", result ? "SUCCESS" : "FAILED");
            return result;
        } catch (Exception e) {
            log.error("[Gemini] API 호출 실패, 자동 승인 처리: {}", e.getMessage(), e);
            return true;
        }
    }

    private boolean parseResult(String response) {
        if (response == null) {
            log.warn("[Gemini] 응답이 null, 자동 승인 처리");
            return true;
        }
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode parts = root.path("candidates").path(0)
                    .path("content").path("parts");

            if (parts.isMissingNode() || parts.isEmpty()) {
                log.warn("[Gemini] 응답에 candidates/parts 없음, 자동 승인 처리: {}", response);
                return true;
            }

            for (JsonNode part : parts) {
                String text = part.path("text").asText().trim().toUpperCase();
                log.info("[Gemini] 응답 텍스트: '{}'", text);
                if (text.contains("FALSE")) return false;
            }
            return true;
        } catch (Exception e) {
            log.error("[Gemini] 응답 파싱 실패, 자동 승인 처리: {}", e.getMessage());
            return true;
        }
    }
}
