package com.netzero.controller;

import com.netzero.config.AuthUtil;
import com.netzero.dto.request.QuestVerifyRequest;
import com.netzero.dto.response.ApiResponse;
import com.netzero.dto.response.QuestResponse;
import com.netzero.service.QuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "퀘스트", description = "일일 퀘스트, 캠퍼스 퀘스트 조회 및 인증 API")
@RestController
@RequestMapping("/api/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;

    @Operation(summary = "일일 퀘스트 목록", description = "매일 0시에 초기화되는 기본 탄소중립 활동 퀘스트 목록을 반환합니다.")
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<List<QuestResponse>>> getDailyQuests() {
        return ResponseEntity.ok(ApiResponse.ok(questService.getDailyQuests(AuthUtil.getCurrentUserId())));
    }

    @Operation(summary = "캠퍼스 퀘스트 목록", description = "인하대학교 캠퍼스 내에서 수행 가능한 미션 목록을 반환합니다.")
    @GetMapping("/campus")
    public ResponseEntity<ApiResponse<List<QuestResponse>>> getCampusQuests() {
        return ResponseEntity.ok(ApiResponse.ok(questService.getCampusQuests(AuthUtil.getCurrentUserId())));
    }

    @Operation(summary = "퀘스트 인증", description = "사진과 게시글 내용을 업로드하여 퀘스트를 인증합니다. 성공 시 GP/XP가 지급되고 그룹 타임라인에 게시됩니다.")
    @PostMapping("/{questId}/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyQuest(
            @Parameter(description = "인증할 퀘스트 ID") @PathVariable Long questId,
            @Valid @RequestBody QuestVerifyRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("퀘스트 인증이 완료되었습니다.",
                questService.verifyQuest(AuthUtil.getCurrentUserId(), questId,
                        request.getVerificationImageUrl(), request.getContent())));
    }
}
