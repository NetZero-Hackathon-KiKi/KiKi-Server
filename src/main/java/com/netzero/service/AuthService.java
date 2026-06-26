package com.netzero.service;

import com.netzero.config.JwtTokenProvider;
import com.netzero.dto.request.LoginRequest;
import com.netzero.dto.request.SignUpRequest;
import com.netzero.entity.User;
import com.netzero.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Map<String, Object> signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .university(request.getUniversity())
                .department(request.getDepartment())
                .build();

        User saved = userRepository.save(user);

        return Map.of(
                "userId", saved.getId(),
                "nickname", saved.getNickname()
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        return Map.of(
                "userId", user.getId(),
                "nickname", user.getNickname(),
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }

    public Map<String, String> refresh(String refreshToken) {
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }
        Long userId = jwtTokenProvider.getUserId(refreshToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);

        return Map.of("accessToken", newAccessToken);
    }
}
