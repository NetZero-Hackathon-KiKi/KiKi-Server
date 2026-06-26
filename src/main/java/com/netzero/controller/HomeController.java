package com.netzero.controller;

import com.netzero.dto.response.ApiResponse;
import com.netzero.entity.Attack;
import com.netzero.entity.Quest;
import com.netzero.entity.User;
import com.netzero.repository.UserRepository;
import com.netzero.service.AttackService;
import com.netzero.service.QuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Home", description = "홈 화면 API")
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final UserRepository userRepository;
    private final AttackService attackService;
    private final QuestService questService;

    @Operation(summary = "홈 화면 조회", description = "유저 프로필, 현재 공격 미션, 일일 퀘스트를 포함한 홈 화면 데이터를 조회합니다.")
    @GetMapping
    public ApiResponse<?> getHome(@Parameter(description = "유저 ID") @RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        Map<String, Object> profile = Map.of(
                "userId", user.getId(),
                "nickname", user.getNickname(),
                "gp", user.getGp(),
                "xp", user.getXp(),
                "profileImageUrl", user.getProfileImageUrl() != null ? user.getProfileImageUrl() : ""
        );

        Attack currentAttack = attackService.getCurrentAttack(userId);
        Map<String, Object> attackMission = null;
        if (currentAttack != null) {
            Quest quest = currentAttack.getQuest();
            long remainingSeconds = Duration.between(LocalDateTime.now(), currentAttack.getDeadline()).getSeconds();
            attackMission = new HashMap<>();
            attackMission.put("attackId", currentAttack.getId());
            attackMission.put("questTitle", quest.getTitle());
            attackMission.put("questDescription", quest.getDescription());
            attackMission.put("attackerNickname", currentAttack.getAttacker().getNickname());
            attackMission.put("remainingSeconds", Math.max(0, remainingSeconds));
        }

        List<Quest> dailyQuests = questService.getQuestsByType("DAILY");

        Map<String, Object> result = new HashMap<>();
        result.put("profile", profile);
        result.put("currentAttackMission", attackMission);
        result.put("dailyQuests", dailyQuests);

        return ApiResponse.ok(result);
    }
}
