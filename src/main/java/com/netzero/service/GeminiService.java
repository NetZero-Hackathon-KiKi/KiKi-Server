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

        String prompt = String.join("\n",
                "You are a strict mission verification judge.",
                "",
                "Mission title: \"" + questTitle + "\"",
                "Mission description: \"" + questDescription + "\"",
                "",
                "Analyze the uploaded photo and determine whether it is valid proof of completing the above mission.",
                "",
                "Rules:",
                "- The photo MUST clearly show evidence directly related to the mission.",
                "- If the mission is about using a tumbler, the photo must show a tumbler actually being used.",
                "- If the mission is about riding a bicycle, the photo must show a bicycle being ridden or parked after riding.",
                "- If the mission is about picking up trash, the photo must show collected trash or the act of picking it up.",
                "- If the mission is about turning off lights, the photo must show lights turned off or a dark room.",
                "- Random, irrelevant, or unrelated photos must be rejected.",
                "- Photos that could apply to any mission (e.g. a selfie, a blank wall, a random object) must be rejected.",
                "",
                "Respond with ONLY one word: TRUE if the photo is valid proof, FALSE if not."
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
            String response = webClient.post()
                    .uri("/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseResult(response);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean parseResult(String response) {
        if (response == null) return false;
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode parts = root.path("candidates").path(0)
                    .path("content").path("parts");
            for (JsonNode part : parts) {
                String text = part.path("text").asText().trim().toUpperCase();
                if (text.contains("TRUE")) return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
