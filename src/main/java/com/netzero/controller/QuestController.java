package com.netzero.controller;

import com.netzero.dto.response.ApiResponse;
import com.netzero.service.QuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Quest", description = "퀘스트 관련 API")
@RestController
@RequestMapping("/api/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;

    @Operation(summary = "퀘스트 목록 조회", description = "타입별(DAILY/CAMPUS) 퀘스트 목록을 유저별 완료 여부와 함께 조회합니다.")
    @GetMapping
    public ApiResponse<?> getQuests(@Parameter(description = "유저 ID") @RequestParam Long userId,
                                    @Parameter(description = "퀘스트 타입 (DAILY / CAMPUS)") @RequestParam String type) {
        return ApiResponse.ok(questService.getQuestsByTypeAndUser(userId, type));
    }

    @Operation(summary = "퀘스트 인증", description = "사진을 업로드하여 퀘스트를 인증합니다. Gemini AI로 검증 후 타임라인에 게시됩니다.")
    @PostMapping("/{questId}/verify")
    public ApiResponse<?> verifyQuest(@Parameter(description = "유저 ID") @RequestParam Long userId,
                                       @Parameter(description = "퀘스트 ID") @PathVariable Long questId,
                                       @Parameter(description = "인증 사진") @RequestParam("image") MultipartFile image) {
        try {
            return ApiResponse.ok(questService.verifyAndComplete(userId, questId, image));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
