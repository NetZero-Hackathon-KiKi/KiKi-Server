package com.netzero.controller;

import com.netzero.dto.request.AttackRequest;
import com.netzero.dto.response.ApiResponse;
import com.netzero.service.AttackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attacks")
@RequiredArgsConstructor
public class AttackController {

    private final AttackService attackService;

    @GetMapping("/targets")
    public ApiResponse<?> getTargets(@RequestParam Long userId) {
        return ApiResponse.ok(attackService.getTargets(userId));
    }

    @GetMapping("/missions")
    public ApiResponse<?> getMissions() {
        return ApiResponse.ok(attackService.getMissions());
    }

    @PostMapping
    public ApiResponse<?> attack(@RequestParam Long userId, @RequestBody AttackRequest request) {
        try {
            return ApiResponse.ok(attackService.attack(userId, request.getTargetIds(), request.getQuestId()));
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/received")
    public ApiResponse<?> getReceivedAttacks(@RequestParam Long userId) {
        return ApiResponse.ok(attackService.getReceivedAttacks(userId));
    }

    @GetMapping("/received/current")
    public ApiResponse<?> getCurrentAttack(@RequestParam Long userId) {
        return ApiResponse.ok(attackService.getCurrentAttack(userId));
    }

    @GetMapping("/sent")
    public ApiResponse<?> getSentAttacks(@RequestParam Long userId) {
        return ApiResponse.ok(attackService.getSentAttacks(userId));
    }
}
