package com.netzero.controller;

import com.netzero.config.AuthUtil;
import com.netzero.dto.response.ApiResponse;
import com.netzero.dto.response.RankingResponse;
import com.netzero.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "랭킹", description = "교내 전체 랭킹, 학과 랭킹, 내 순위 조회 API")
@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @Operation(summary = "교내 전체 랭킹", description = "같은 대학교 전체 학생의 GP 기준 랭킹을 반환합니다.")
    @GetMapping("/university")
    public ResponseEntity<ApiResponse<RankingResponse>> getUniversityRanking(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(rankingService.getUniversityRanking(AuthUtil.getCurrentUserId(), page, size)));
    }

    @Operation(summary = "학과 랭킹", description = "같은 학과 학생의 GP 기준 랭킹을 반환합니다.")
    @GetMapping("/department")
    public ResponseEntity<ApiResponse<RankingResponse>> getDepartmentRanking(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(rankingService.getDepartmentRanking(AuthUtil.getCurrentUserId(), page, size)));
    }

    @Operation(summary = "내 순위 조회", description = "교내 전체 순위, 학과 내 순위, GP, 순위 변동을 반환합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyRanking() {
        return ResponseEntity.ok(ApiResponse.ok(rankingService.getMyRanking(AuthUtil.getCurrentUserId())));
    }
}
