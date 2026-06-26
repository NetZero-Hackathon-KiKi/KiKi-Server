package com.netzero.controller;

import com.netzero.config.AuthUtil;
import com.netzero.dto.request.AttackRequest;
import com.netzero.dto.request.QuestVerifyRequest;
import com.netzero.dto.response.*;
import com.netzero.service.AttackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "공격", description = "공격 미션 보내기/받기, 인증, 그룹원 인증 API")
@RestController
@RequestMapping("/api/attacks")
@RequiredArgsConstructor
public class AttackController {

    private final AttackService attackService;

    @Operation(summary = "공격 보내기", description = "그룹 내 친구에게 공격 미션을 보냅니다. 소모 포인트가 차감됩니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendAttack(
            @Valid @RequestBody AttackRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("공격을 보냈습니다!",
                attackService.sendAttack(AuthUtil.getCurrentUserId(), request)));
    }

    @Operation(summary = "공격 대상 목록", description = "그룹 멤버 중 공격 가능한 대상 목록을 반환합니다.")
    @GetMapping("/targets")
    public ResponseEntity<ApiResponse<List<AttackTargetResponse>>> getAttackTargets() {
        return ResponseEntity.ok(ApiResponse.ok(attackService.getAttackTargets(AuthUtil.getCurrentUserId())));
    }

    @Operation(summary = "공격 미션 선택 목록", description = "공격 시 선택 가능한 미션 목록을 반환합니다.")
    @GetMapping("/missions")
    public ResponseEntity<ApiResponse<List<QuestResponse>>> getAttackMissions() {
        return ResponseEntity.ok(ApiResponse.ok(attackService.getAttackMissions()));
    }

    @Operation(summary = "받은 공격 목록", description = "내가 받은 공격 미션 전체 목록을 반환합니다.")
    @GetMapping("/received")
    public ResponseEntity<ApiResponse<List<AttackMissionResponse>>> getReceivedAttacks() {
        return ResponseEntity.ok(ApiResponse.ok(attackService.getReceivedAttacks(AuthUtil.getCurrentUserId())));
    }

    @Operation(summary = "현재 진행 중인 공격 미션", description = "홈 화면에 표시할 현재 진행 중인 공격 미션을 반환합니다.")
    @GetMapping("/received/current")
    public ResponseEntity<ApiResponse<AttackMissionResponse>> getCurrentAttackMission() {
        return ResponseEntity.ok(ApiResponse.ok(attackService.getCurrentAttackMission(AuthUtil.getCurrentUserId())));
    }

    @Operation(summary = "보낸 공격 목록", description = "내가 보낸 공격 미션 전체 목록을 반환합니다.")
    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<List<AttackMissionResponse>>> getSentAttacks() {
        return ResponseEntity.ok(ApiResponse.ok(attackService.getSentAttacks(AuthUtil.getCurrentUserId())));
    }

    @Operation(summary = "공격 미션 인증", description = "받은 공격 미션을 사진과 내용으로 인증합니다. 인증 후 그룹원 2명 이상의 승인이 필요합니다.")
    @PostMapping("/{attackId}/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyAttackMission(
            @Parameter(description = "공격 ID") @PathVariable Long attackId,
            @Valid @RequestBody QuestVerifyRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("미션 인증이 완료되었습니다.",
                attackService.verifyAttackMission(AuthUtil.getCurrentUserId(), attackId,
                        request.getVerificationImageUrl(), request.getContent())));
    }

    @Operation(summary = "그룹원 인증", description = "그룹원이 공격 미션 인증 사진을 승인/거절합니다. 2명 이상 승인 시 방어 성공.")
    @PostMapping("/{attackId}/group-verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> groupVerifyAttack(
            @Parameter(description = "공격 ID") @PathVariable Long attackId,
            @Parameter(description = "승인 여부") @RequestParam boolean approved) {
        return ResponseEntity.ok(ApiResponse.ok("인증이 완료되었습니다.",
                attackService.groupVerifyAttack(AuthUtil.getCurrentUserId(), attackId, approved)));
    }

    @Operation(summary = "공격 상세 조회", description = "인증 요청 상세 화면 - 인증 사진, 내용, 인증자 목록을 반환합니다.")
    @GetMapping("/{attackId}/detail")
    public ResponseEntity<ApiResponse<AttackDetailResponse>> getAttackDetail(
            @Parameter(description = "공격 ID") @PathVariable Long attackId) {
        return ResponseEntity.ok(ApiResponse.ok(attackService.getAttackDetail(attackId)));
    }
}
