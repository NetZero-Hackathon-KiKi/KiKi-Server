package com.netzero.controller;

import com.netzero.dto.response.ApiResponse;
import com.netzero.service.QuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;

    // 퀘스트 목록 (DAILY / CAMPUS)
    @GetMapping
    public ApiResponse<?> getQuests(@RequestParam String type) {
        return ApiResponse.ok(questService.getQuestsByType(type));
    }

    // 퀘스트 수행 + 사진 업로드 → Gemini 검증 → 타임라인 게시
    @PostMapping("/{questId}/verify")
    public ApiResponse<?> verifyQuest(@RequestParam Long userId,
                                       @PathVariable Long questId,
                                       @RequestParam("image") MultipartFile image) {
        try {
            return ApiResponse.ok(questService.verifyAndComplete(userId, questId, image));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
