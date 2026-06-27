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

        String prompt = "당신은 환경 보호 미션의 사진 인증을 판정하는 AI입니다.\n\n" +
                "[미션 정보]\n" +
                "제목: " + questTitle + "\n" +
                "설명: " + questDescription + "\n\n" +
                "[규칙]\n" +
                "1. 사진에 미션과 관련된 물체가 하나라도 보이면 TRUE입니다.\n" +
                "2. 배경, 장소, 상황은 중요하지 않습니다. 물체만 보이면 됩니다.\n" +
                "3. 미션과 완전히 무관한 사진만 FALSE입니다.\n\n" +
                "[미션별 TRUE 조건]\n" +
                "텀블러사용하기: 텀블러, 텀블러컵, 보온병, 머그컵, 리유저블컵, 개인컵, 스텐컵, 보냉병, 물병, 스타벅스텀블러 등 일회용이 아닌 컵/병이 보이면 TRUE\n" +
                "분리수거하기: 분리수거함, 재활용 쓰레기, 분리된 쓰레기, 재활용 마크가 보이면 TRUE\n" +
                "안쓰는 멀티탭 뽑기: 멀티탭, 콘센트, 전원 스위치, 플러그가 보이면 TRUE\n" +
                "음식물 남기지 않기: 빈 그릇, 깨끗한 식판, 다 먹은 음식 사진이면 TRUE\n" +
                "대중교통 이용하기: 버스, 지하철, 전철, 기차, 교통카드, 버스정류장, 지하철역이 보이면 TRUE\n" +
                "캠퍼스 플로깅: 쓰레기봉투, 쓰레기 줍기, 집게, 야외 청소 장면이 보이면 TRUE\n" +
                "에너지 절약 캠페인: 에너지 절약 관련 포스터, 캠페인, 행사 장면이 보이면 TRUE\n" +
                "자전거 출퇴근: 자전거가 보이면 TRUE\n\n" +
                "TRUE 또는 FALSE 한 단어만 출력하세요.";

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
