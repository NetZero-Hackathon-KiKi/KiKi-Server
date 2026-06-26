package com.netzero.service;

import com.netzero.dto.request.UpdateNicknameRequest;
import com.netzero.dto.request.UpdateNotificationRequest;
import com.netzero.dto.response.*;
import com.netzero.entity.User;
import com.netzero.repository.FriendshipRepository;
import com.netzero.repository.NotificationRepository;
import com.netzero.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final NotificationRepository notificationRepository;
    private final QuestService questService;
    private final AttackService attackService;

    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(Long userId) {
        User user = getUser(userId);
        int friendCount = friendshipRepository.countFriends(userId);

        return UserProfileResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .university(user.getUniversity())
                .department(user.getDepartment())
                .level(user.getLevel())
                .currentXp(user.getCurrentXp())
                .maxXp(user.getMaxXp())
                .greenPoint(user.getGreenPoint())
                .friendCount(friendCount)
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    @Transactional
    public HomeResponse getHomeData(Long userId) {
        UserProfileResponse profile = getMyProfile(userId);
        AttackMissionResponse currentAttack = attackService.getCurrentAttackMission(userId);
        int unreadCount = notificationRepository.countByUserIdAndReadFalse(userId);

        return HomeResponse.builder()
                .profile(profile)
                .unreadNotificationCount(unreadCount)
                .currentAttackMission(currentAttack)
                .dailyQuests(questService.getDailyQuests(userId))
                .campusQuests(questService.getCampusQuests(userId))
                .build();
    }

    @Transactional
    public void updateNickname(Long userId, UpdateNicknameRequest request) {
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
        User user = getUser(userId);
        user.setNickname(request.getNickname());
        userRepository.save(user);
    }

    @Transactional
    public void updateNotificationSettings(Long userId, UpdateNotificationRequest request) {
        User user = getUser(userId);
        if (request.getAttackNotification() != null) {
            user.setAttackNotification(request.getAttackNotification());
        }
        if (request.getQuestNotification() != null) {
            user.setQuestNotification(request.getQuestNotification());
        }
        if (request.getVerificationNotification() != null) {
            user.setVerificationNotification(request.getVerificationNotification());
        }
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public SettingsResponse getSettings(Long userId) {
        User user = getUser(userId);
        return SettingsResponse.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .university(user.getUniversity())
                .department(user.getDepartment())
                .profileImageUrl(user.getProfileImageUrl())
                .attackNotification(user.isAttackNotification())
                .questNotification(user.isQuestNotification())
                .verificationNotification(user.isVerificationNotification())
                .build();
    }
}
