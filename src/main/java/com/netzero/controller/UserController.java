package com.netzero.controller;

import com.netzero.dto.response.ApiResponse;
import com.netzero.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // 유저 정보 조회
    @GetMapping("/{userId}")
    public ApiResponse<?> getUser(@PathVariable Long userId) {
        return ApiResponse.ok(userRepository.findById(userId).orElseThrow());
    }
}
