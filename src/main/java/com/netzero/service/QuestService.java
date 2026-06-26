package com.netzero.service;

import com.netzero.dto.response.QuestResponse;
import com.netzero.entity.*;
import com.netzero.repository.QuestRepository;
import com.netzero.repository.TimelinePostRepository;
import com.netzero.repository.UserQuestRepository;
import com.netzero.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;
    private final UserQuestRepository userQuestRepository;
    private final UserRepository userRepository;
    private final TimelinePostRepository timelinePostRepository;
    private final GeminiService geminiService;

    @Transactional
    public List<QuestResponse> getDailyQuests(Long userId) {
        return getQuestsByType(userId, Quest.QuestType.DAILY);
    }

    @Transactional
    public List<QuestResponse> getCampusQuests(Long userId) {
        return getQuestsByType(userId, Quest.QuestType.CAMPUS);
    }

    private List<QuestResponse> getQuestsByType(Long userId, Quest.QuestType type) {
        List<Quest> quests = questRepository.findByType(type);
        LocalDate today = LocalDate.now();
        List<UserQuest> userQuests = userQuestRepository.findByUserIdAndAssignedDate(userId, today);

        Map<Long, UserQuest> userQuestMap = userQuests.stream()
                .collect(Collectors.toMap(uq -> uq.getQuest().getId(), uq -> uq));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return quests.stream().map(quest -> {
            UserQuest uq = userQuestMap.get(quest.getId());
            if (uq == null) {
                uq = UserQuest.builder()
                        .user(user)
                        .quest(quest)
                        .assignedDate(today)
                        .build();
                userQuestRepository.save(uq);
            }
            return QuestResponse.builder()
                    .questId(quest.getId())
                    .title(quest.getTitle())
                    .description(quest.getDescription())
                    .type(quest.getType().name())
                    .rewardGp(quest.getRewardGp())
                    .rewardXp(quest.getRewardXp())
                    .currentCount(uq.getCurrentCount())
                    .requiredCount(uq.getRequiredCount())
                    .completed(uq.isCompleted())
                    .location(quest.getLocation())
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> verifyQuest(Long userId, Long questId,
                                            String verificationImageUrl, String content) {
        LocalDate today = LocalDate.now();
        UserQuest userQuest = userQuestRepository.findByUserIdAndQuestIdAndAssignedDate(userId, questId, today)
                .orElseThrow(() -> new IllegalArgumentException("해당 퀘스트를 찾을 수 없습니다."));

        if (userQuest.isCompleted()) {
            throw new IllegalStateException("이미 완료된 퀘스트입니다.");
        }

        Quest quest = userQuest.getQuest();

        // AI 이미지 검증
        boolean verified = geminiService.verifyQuestImage(
                verificationImageUrl, quest.getTitle(), quest.getDescription());
        if (!verified) {
            throw new IllegalStateException("AI 인증 실패: 사진이 퀘스트와 일치하지 않습니다.");
        }

        userQuest.setCurrentCount(userQuest.getCurrentCount() + 1);
        userQuest.setVerificationImageUrl(verificationImageUrl);

        User user = userRepository.findById(userId).orElseThrow();

        if (userQuest.getCurrentCount() >= userQuest.getRequiredCount()) {
            userQuest.setCompleted(true);
            userQuest.setCompletedAt(LocalDateTime.now());

            user.setGreenPoint(user.getGreenPoint() + quest.getRewardGp());
            user.setCurrentXp(user.getCurrentXp() + quest.getRewardXp());
            checkLevelUp(user);
            userRepository.save(user);
        }

        userQuestRepository.save(userQuest);

        if (user.getGroup() != null) {
            TimelinePost post = TimelinePost.builder()
                    .author(user)
                    .group(user.getGroup())
                    .content(content)
                    .imageUrl(verificationImageUrl)
                    .verificationType(TimelinePost.VerificationType.QUEST)
                    .verificationInfo("quest:" + questId)
                    .build();
            timelinePostRepository.save(post);
        }

        return Map.of(
                "questId", questId,
                "earnedGp", quest.getRewardGp(),
                "earnedXp", quest.getRewardXp(),
                "completed", userQuest.isCompleted()
        );
    }

    private void checkLevelUp(User user) {
        while (user.getCurrentXp() >= user.getMaxXp()) {
            user.setCurrentXp(user.getCurrentXp() - user.getMaxXp());
            user.setLevel(user.getLevel() + 1);
            user.setMaxXp((int) (user.getMaxXp() * 1.2));
        }
    }
}
