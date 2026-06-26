package com.netzero.controller;

import com.netzero.dto.request.AttackRequest;
import com.netzero.dto.response.ApiResponse;
import com.netzero.service.AttackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Attack", description = "공격 관련 API")
@RestController
@RequestMapping("/api/attacks")
@RequiredArgsConstructor
public class AttackController {

    private final AttackService attackService;

    @Operation(summary = "공격 대상 목록 조회", description = "공격할 수 있는 대상 유저 목록을 조회합니다.")
    @GetMapping("/targets")
    public ApiResponse<?> getTargets(@Parameter(description = "유저 ID") @RequestParam Long userId) {
        return ApiResponse.ok(attackService.getTargets(userId));
    }

    @Operation(summary = "공격 미션 목록 조회", description = "공격 시 부여할 수 있는 미션(퀘스트) 목록을 조회합니다.")
    @GetMapping("/missions")
    public ApiResponse<?> getMissions() {
        return ApiResponse.ok(attackService.getMissions());
    }

    @Operation(summary = "공격 실행", description = "대상 유저들에게 퀘스트를 부여하는 공격을 실행합니다.")
    @PostMapping
    public ApiResponse<?> attack(@Parameter(description = "공격자 유저 ID") @RequestParam Long userId,
                                 @RequestBody AttackRequest request) {
        try {
            return ApiResponse.ok(attackService.attack(userId, request.getTargetIds(), request.getQuestId()));
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @Operation(summary = "받은 공격 목록 조회", description = "해당 유저가 받은 공격 목록을 조회합니다.")
    @GetMapping("/received")
    public ApiResponse<?> getReceivedAttacks(@Parameter(description = "유저 ID") @RequestParam Long userId) {
        return ApiResponse.ok(attackService.getReceivedAttacks(userId));
    }

    @Operation(summary = "현재 진행 중인 공격 조회", description = "해당 유저가 현재 받고 있는 공격을 조회합니다.")
    @GetMapping("/received/current")
    public ApiResponse<?> getCurrentAttack(@Parameter(description = "유저 ID") @RequestParam Long userId) {
        return ApiResponse.ok(attackService.getCurrentAttack(userId));
    }

    @Operation(summary = "보낸 공격 목록 조회", description = "해당 유저가 보낸 공격 목록을 조회합니다.")
    @GetMapping("/sent")
    public ApiResponse<?> getSentAttacks(@Parameter(description = "유저 ID") @RequestParam Long userId) {
        return ApiResponse.ok(attackService.getSentAttacks(userId));
    }
}
