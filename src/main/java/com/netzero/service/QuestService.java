package com.netzero.service;

import com.netzero.dto.response.QuestResponse;
import com.netzero.entity.*;
import com.netzero.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;
    private final UserQuestRepository userQuestRepository;
    private final UserRepository userRepository;
    private final TimelinePostRepository timelinePostRepository;
    private final S3Service s3Service;
    private final GeminiService geminiService;

    public List<Quest> getQuestsByType(String type) {
        return questRepository.findByType(type);
    }

    public List<QuestResponse> getQuestsByTypeAndUser(Long userId, String type) {
        List<Quest> quests = questRepository.findByType(type);

        Set<Long> completedQuestIds = userQuestRepository.findByUserIdAndStatus(userId, "SUCCESS")
                .stream()
                .map(uq -> uq.getQuest().getId())
                .collect(Collectors.toSet());

        return quests.stream()
                .map(q -> QuestResponse.builder()
                        .id(q.getId())
                        .title(q.getTitle())
                        .description(q.getDescription())
                        .type(q.getType())
                        .rewardXp(q.getRewardXp())
                        .rewardGp(q.getRewardGp())
                        .completed(completedQuestIds.contains(q.getId()))
                        .build())
                .toList();
    }

    // 퀘스트 수행: 사진 업로드 → S3 저장 → Gemini 검증 → 성공 시 타임라인 게시 + 보상 지급
    @Transactional
    public UserQuest verifyAndComplete(Long userId, Long questId, MultipartFile image) throws IOException {
        User user = userRepository.findById(userId).orElseThrow();
        Quest quest = questRepository.findById(questId).orElseThrow();

        // S3 업로드
        String imageUrl = s3Service.upload(image);

        // Gemini AI 검증 (원본 이미지 바이트를 전달)
        boolean verified = geminiService.verifyQuestImage(image.getBytes(), image.getContentType(), quest.getDescription());

        String status = verified ? "SUCCESS" : "FAILED";

        UserQuest userQuest = UserQuest.builder()
                .user(user)
                .quest(quest)
                .imageUrl(imageUrl)
                .status(status)
                .completedAt(LocalDateTime.now())
                .build();
        userQuestRepository.save(userQuest);

        if (verified) {
            // 보상 지급
            user.setXp(user.getXp() + quest.getRewardXp());
            user.setGp(user.getGp() + quest.getRewardGp());
            userRepository.save(user);

            // 타임라인에 게시
            TimelinePost post = TimelinePost.builder()
                    .user(user)
                    .questTitle(quest.getTitle())
                    .imageUrl(imageUrl)
                    .createdAt(LocalDateTime.now())
                    .build();
            timelinePostRepository.save(post);
        }

        return userQuest;
    }
}
