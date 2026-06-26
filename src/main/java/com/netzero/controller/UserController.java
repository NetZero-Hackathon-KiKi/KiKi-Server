package com.netzero.controller;

import com.netzero.dto.response.ApiResponse;
import com.netzero.dto.response.UserProfileResponse;
import com.netzero.entity.User;
import com.netzero.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "유저 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @Operation(summary = "내 프로필 조회", description = "홈 화면용 프로필 정보를 조회합니다.")
    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getMyProfile(@Parameter(description = "유저 ID") @RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        int level = user.getXp() / 100 + 1;
        int currentXp = user.getXp() % 100;
        int maxXp = 100;

        UserProfileResponse profile = UserProfileResponse.builder()
                .userId(user.getId())
                .name(user.getNickname())
                .level(level)
                .currentXp(currentXp)
                .maxXp(maxXp)
                .gp(user.getGp())
                .friendCount(0)
                .profileImageUrl(user.getProfileImageUrl())
                .build();

        return ApiResponse.ok(profile);
    }

    @Operation(summary = "유저 정보 조회", description = "유저 ID로 유저 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public ApiResponse<?> getUser(@Parameter(description = "유저 ID") @PathVariable Long userId) {
        return ApiResponse.ok(userRepository.findById(userId).orElseThrow());
    }
}
