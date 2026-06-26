package com.netzero.controller;

import com.netzero.config.AuthUtil;
import com.netzero.dto.request.UpdateNicknameRequest;
import com.netzero.dto.request.UpdateNotificationRequest;
import com.netzero.dto.response.*;
import com.netzero.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자", description = "프로필 조회, 홈 화면, 설정 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 프로필 조회", description = "닉네임, 레벨, XP, GP, 친구 수 등 프로필 정보를 반환합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getMyProfile(AuthUtil.getCurrentUserId())));
    }

    @Operation(summary = "홈 화면 전체 데이터", description = "프로필, 현재 공격 미션, 일일 퀘스트, 캠퍼스 퀘스트를 한 번에 반환합니다.")
    @GetMapping("/me/home")
    public ResponseEntity<ApiResponse<HomeResponse>> getHomeData() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getHomeData(AuthUtil.getCurrentUserId())));
    }

    @Operation(summary = "닉네임 변경", description = "사용자의 닉네임을 변경합니다. (2~10자)")
    @PutMapping("/me/nickname")
    public ResponseEntity<ApiResponse<Void>> updateNickname(
            @Valid @RequestBody UpdateNicknameRequest request) {
        userService.updateNickname(AuthUtil.getCurrentUserId(), request);
        return ResponseEntity.ok(ApiResponse.ok("닉네임이 변경되었습니다.", null));
    }

    @Operation(summary = "알림 설정 변경", description = "공격 알림, 퀘스트 알림, 인증 알림의 ON/OFF를 변경합니다.")
    @PutMapping("/me/notifications")
    public ResponseEntity<ApiResponse<Void>> updateNotificationSettings(
            @Valid @RequestBody UpdateNotificationRequest request) {
        userService.updateNotificationSettings(AuthUtil.getCurrentUserId(), request);
        return ResponseEntity.ok(ApiResponse.ok("알림 설정이 변경되었습니다.", null));
    }

    @Operation(summary = "설정 화면 데이터", description = "닉네임, 이메일, 학교/학과, 알림 설정 등 설정 화면에 필요한 전체 데이터를 반환합니다.")
    @GetMapping("/me/settings")
    public ResponseEntity<ApiResponse<SettingsResponse>> getSettings() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getSettings(AuthUtil.getCurrentUserId())));
    }
}
