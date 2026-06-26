package com.netzero.service;

import com.netzero.entity.Attack;
import com.netzero.entity.Quest;
import com.netzero.entity.User;
import com.netzero.repository.AttackRepository;
import com.netzero.repository.QuestRepository;
import com.netzero.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttackService {

    private final AttackRepository attackRepository;
    private final UserRepository userRepository;
    private final QuestRepository questRepository;

    private static final int ATTACK_COST = 50;

    public List<User> getTargets(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return userRepository.findByDepartmentAndUniversityOrderByGpDesc(user.getDepartment(), user.getUniversity())
                .stream().filter(u -> !u.getId().equals(userId)).toList();
    }

    public List<Quest> getMissions() {
        return questRepository.findAll();
    }

    @Transactional
    public List<Attack> attack(Long attackerId, List<Long> targetIds, Long questId) {
        User attacker = userRepository.findById(attackerId).orElseThrow();
        Quest quest = questRepository.findById(questId).orElseThrow();

        int totalCost = ATTACK_COST * targetIds.size();
        if (attacker.getGp() < totalCost) {
            throw new RuntimeException("GP가 부족합니다. (필요: " + totalCost + "GP)");
        }

        attacker.setGp(attacker.getGp() - totalCost);
        userRepository.save(attacker);

        List<Attack> attacks = new ArrayList<>();
        for (Long targetId : targetIds) {
            User target = userRepository.findById(targetId).orElseThrow();
            if (!attacker.getDepartment().equals(target.getDepartment())) {
                throw new RuntimeException("같은 학과만 공격 가능합니다.");
            }

            Attack attack = Attack.builder()
                    .attacker(attacker)
                    .target(target)
                    .quest(quest)
                    .completed(false)
                    .deadline(LocalDateTime.now().plusHours(24))
                    .createdAt(LocalDateTime.now())
                    .build();
            attacks.add(attackRepository.save(attack));
        }

        return attacks;
    }

    public List<Attack> getReceivedAttacks(Long userId) {
        return attackRepository.findByTargetIdOrderByCreatedAtDesc(userId);
    }

    public Attack getCurrentAttack(Long userId) {
        List<Attack> pending = attackRepository.findByTargetIdAndCompletedFalseOrderByCreatedAtDesc(userId);
        return pending.isEmpty() ? null : pending.get(0);
    }

    public List<Attack> getSentAttacks(Long userId) {
        return attackRepository.findByAttackerIdOrderByCreatedAtDesc(userId);
    }
}
