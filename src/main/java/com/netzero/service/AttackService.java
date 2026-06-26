package com.netzero.service;

import com.netzero.dto.request.AttackRequest;
import com.netzero.dto.response.*;
import com.netzero.entity.*;
import com.netzero.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttackService {

    private final AttackRepository attackRepository;
    private final AttackVerificationRepository attackVerificationRepository;
    private final UserRepository userRepository;
    private final QuestRepository questRepository;
    private final TimelinePostRepository timelinePostRepository;
    private final NotificationService notificationService;

    @Transactional
    public Map<String, Object> sendAttack(Long attackerId, AttackRequest request) {
        User attacker = userRepository.findById(attackerId)
                .orElseThrow(() -> new IllegalArgumentException("공격자를 찾을 수 없습니다."));
        User defender = userRepository.findById(request.getTargetUserId())
                .orElseThrow(() -> new IllegalArgumentException("대상 사용자를 찾을 수 없습니다."));
        Quest quest = questRepository.findById(request.getQuestId())
                .orElseThrow(() -> new IllegalArgumentException("퀘스트를 찾을 수 없습니다."));

        if (attacker.getGreenPoint() < request.getGpAtStake()) {
            throw new IllegalStateException("GP가 부족합니다.");
        }

        attacker.setGreenPoint(attacker.getGreenPoint() - request.getGpAtStake());
        userRepository.save(attacker);

        Attack attack = Attack.builder()
                .attacker(attacker)
                .defender(defender)
                .missionQuest(quest)
                .gpAtStake(request.getGpAtStake())
                .deadline(LocalDateTime.now().plusHours(24))
                .build();

        Attack saved = attackRepository.save(attack);

        notificationService.createNotification(
                defender,
                "공격 미션 도착!",
                attacker.getNickname() + "님이 " + quest.getTitle() + " 미션을 보냈어요.",
                Notification.NotificationType.ATTACK,
                saved.getId(),
                attacker
        );

        return Map.of(
                "attackId", saved.getId(),
                "targetNickname", defender.getNickname(),
                "missionTitle", quest.getTitle(),
                "remainingGp", attacker.getGreenPoint()
        );
    }

    @Transactional(readOnly = true)
    public List<AttackMissionResponse> getReceivedAttacks(Long userId) {
        return attackRepository.findByDefenderIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AttackMissionResponse getCurrentAttackMission(Long userId) {
        List<Attack> pending = attackRepository.findByDefenderIdAndStatus(userId, Attack.AttackStatus.PENDING);
        if (pending.isEmpty()) {
            pending = attackRepository.findByDefenderIdAndStatus(userId, Attack.AttackStatus.VERIFIED);
        }
        if (pending.isEmpty()) return null;
        return toResponse(pending.get(0));
    }

    @Transactional(readOnly = true)
    public List<AttackMissionResponse> getSentAttacks(Long userId) {
        return attackRepository.findByAttackerIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> verifyAttackMission(Long userId, Long attackId,
                                                    String imageUrl, String content) {
        Attack attack = attackRepository.findById(attackId)
                .orElseThrow(() -> new IllegalArgumentException("공격을 찾을 수 없습니다."));

        if (!attack.getDefender().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 공격 미션만 인증할 수 있습니다.");
        }
        if (attack.getStatus() != Attack.AttackStatus.PENDING) {
            throw new IllegalStateException("인증 가능한 상태가 아닙니다.");
        }

        attack.setVerificationImageUrl(imageUrl);
        attack.setVerificationContent(content);
        attack.setStatus(Attack.AttackStatus.VERIFIED);
        attackRepository.save(attack);

        User defender = attack.getDefender();
        if (defender.getGroup() != null) {
            TimelinePost post = TimelinePost.builder()
                    .author(defender)
                    .group(defender.getGroup())
                    .content(content)
                    .imageUrl(imageUrl)
                    .verificationType(TimelinePost.VerificationType.ATTACK)
                    .verificationInfo("attack:" + attackId)
                    .build();
            timelinePostRepository.save(post);
        }

        return Map.of(
                "attackId", attackId,
                "status", "VERIFIED",
                "groupVerificationCount", attack.getGroupVerificationCount(),
                "requiredGroupVerification", attack.getRequiredGroupVerification()
        );
    }

    @Transactional
    public Map<String, Object> groupVerifyAttack(Long verifierId, Long attackId, boolean approved) {
        Attack attack = attackRepository.findById(attackId)
                .orElseThrow(() -> new IllegalArgumentException("공격을 찾을 수 없습니다."));

        if (attack.getStatus() != Attack.AttackStatus.VERIFIED) {
            throw new IllegalStateException("그룹 인증 가능한 상태가 아닙니다.");
        }
        if (attackVerificationRepository.existsByAttackIdAndVerifierId(attackId, verifierId)) {
            throw new IllegalStateException("이미 인증에 참여했습니다.");
        }

        User verifier = userRepository.findById(verifierId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        AttackVerification verification = AttackVerification.builder()
                .attack(attack)
                .verifier(verifier)
                .approved(approved)
                .build();
        attackVerificationRepository.save(verification);

        String defenseResult = "PENDING";
        if (approved) {
            int approvedCount = attackVerificationRepository.countByAttackIdAndApproved(attackId, true);
            attack.setGroupVerificationCount(approvedCount);

            if (approvedCount >= attack.getRequiredGroupVerification()) {
                attack.setStatus(Attack.AttackStatus.DEFENDED);
                defenseResult = "DEFENDED";

                User defender = attack.getDefender();
                int totalReward = attack.getGpAtStake() * 2;
                defender.setGreenPoint(defender.getGreenPoint() + totalReward);
                userRepository.save(defender);

                notificationService.createNotification(
                        defender,
                        "인증이 승인됐어요",
                        attack.getMissionQuest().getTitle() + " 인증이 승인되어 +"
                                + totalReward + " GP를 획득했어요.",
                        Notification.NotificationType.VERIFICATION,
                        attackId,
                        verifier
                );
            }
        }

        attackRepository.save(attack);

        return Map.of(
                "attackId", attackId,
                "groupVerificationCount", attack.getGroupVerificationCount(),
                "requiredGroupVerification", attack.getRequiredGroupVerification(),
                "defenseResult", defenseResult
        );
    }

    @Transactional(readOnly = true)
    public List<AttackTargetResponse> getAttackTargets(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getGroup() == null) {
            throw new IllegalStateException("그룹에 소속되어야 공격할 수 있습니다.");
        }

        return user.getGroup().getMembers().stream()
                .filter(m -> !m.getId().equals(userId))
                .sorted(Comparator.comparingInt(User::getGreenPoint).reversed())
                .map(m -> AttackTargetResponse.builder()
                        .userId(m.getId())
                        .nickname(m.getNickname())
                        .profileImageUrl(m.getProfileImageUrl())
                        .greenPoint(m.getGreenPoint())
                        .level(m.getLevel())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuestResponse> getAttackMissions() {
        return questRepository.findAll().stream()
                .map(q -> QuestResponse.builder()
                        .questId(q.getId())
                        .title(q.getTitle())
                        .description(q.getDescription())
                        .type(q.getType().name())
                        .rewardGp(q.getRewardGp())
                        .rewardXp(q.getRewardXp())
                        .location(q.getLocation())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AttackDetailResponse getAttackDetail(Long attackId) {
        Attack attack = attackRepository.findById(attackId)
                .orElseThrow(() -> new IllegalArgumentException("공격을 찾을 수 없습니다."));

        List<AttackDetailResponse.VerifierInfo> verifiers = attack.getVerifications().stream()
                .map(v -> AttackDetailResponse.VerifierInfo.builder()
                        .userId(v.getVerifier().getId())
                        .nickname(v.getVerifier().getNickname())
                        .profileImageUrl(v.getVerifier().getProfileImageUrl())
                        .approved(v.isApproved())
                        .build())
                .collect(Collectors.toList());

        Quest quest = attack.getMissionQuest();
        return AttackDetailResponse.builder()
                .attackId(attack.getId())
                .defenderNickname(attack.getDefender().getNickname())
                .defenderProfileImageUrl(attack.getDefender().getProfileImageUrl())
                .questTitle(quest.getTitle())
                .questDescription(quest.getDescription())
                .rewardGp(quest.getRewardGp())
                .rewardXp(quest.getRewardXp())
                .verificationImageUrl(attack.getVerificationImageUrl())
                .verificationContent(attack.getVerificationContent())
                .status(attack.getStatus().name())
                .gpAtStake(attack.getGpAtStake())
                .currentVerificationCount(attack.getGroupVerificationCount())
                .requiredVerificationCount(attack.getRequiredGroupVerification())
                .verifiers(verifiers)
                .createdAt(attack.getCreatedAt())
                .deadline(attack.getDeadline())
                .build();
    }

    private AttackMissionResponse toResponse(Attack attack) {
        long remaining = Duration.between(LocalDateTime.now(), attack.getDeadline()).getSeconds();
        return AttackMissionResponse.builder()
                .attackId(attack.getId())
                .attackerNickname(attack.getAttacker().getNickname())
                .defenderNickname(attack.getDefender().getNickname())
                .missionTitle(attack.getMissionQuest().getTitle())
                .missionDescription(attack.getMissionQuest().getDescription())
                .status(attack.getStatus().name())
                .gpAtStake(attack.getGpAtStake())
                .verificationImageUrl(attack.getVerificationImageUrl())
                .groupVerificationCount(attack.getGroupVerificationCount())
                .requiredGroupVerification(attack.getRequiredGroupVerification())
                .deadline(attack.getDeadline())
                .remainingSeconds(Math.max(0, remaining))
                .build();
    }
}
