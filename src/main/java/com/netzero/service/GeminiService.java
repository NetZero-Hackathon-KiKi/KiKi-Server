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

        String prompt = "너는 탄소중립 실천 미션 인증 사진을 검증하는 AI야.\n\n" +
                "미션: \"" + questDescription + "\"\n\n" +
                "위 미션과 관련된 물체, 행동, 장소가 사진에 포함되어 있으면 인증 성공이야.\n" +
                "엄격하게 판단하지 말고, 미션과 조금이라도 관련이 있으면 성공으로 판단해줘.\n\n" +
                "예시:\n" +
                "- 미션이 '텀블러 사용하기'이면 텀블러, 보온병, 재사용 컵 등이 보이면 성공\n" +
                "- 미션이 '분리수거하기'이면 분리수거함, 재활용품 등이 보이면 성공\n" +
                "- 미션이 '대중교통 이용하기'이면 버스, 지하철, 교통카드 등이 보이면 성공\n\n" +
                "반드시 true 또는 false 중 하나만 답해. 다른 말은 하지 마.";

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
