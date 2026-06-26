package com.netzero.controller;

import com.netzero.dto.request.LoginRequest;
import com.netzero.dto.request.SignUpRequest;
import com.netzero.dto.response.ApiResponse;
import com.netzero.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "인증", description = "회원가입, 로그인, 로그아웃, 토큰 갱신 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임, 학교, 학과 정보로 회원가입합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 중복")
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Map<String, Object>>> signUp(
            @Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("회원가입 성공", authService.signUp(request)));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 JWT 토큰을 반환합니다. 반환된 accessToken을 Swagger 상단 Authorize 버튼에 입력하세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공, JWT 토큰 반환"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이메일 또는 비밀번호 불일치")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("로그인 성공", authService.login(request)));
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.ok("로그아웃 성공", null));
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refresh(
            @Parameter(description = "리프레시 토큰 (Bearer 형식)") @RequestHeader("Authorization") String refreshToken) {
        return ResponseEntity.ok(ApiResponse.ok("토큰 갱신 성공", authService.refresh(refreshToken)));
    }
}
