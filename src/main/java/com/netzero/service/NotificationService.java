package com.netzero.service;

import com.netzero.dto.response.NotificationResponse;
import com.netzero.entity.Notification;
import com.netzero.entity.User;
import com.netzero.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId, int page, int size) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> getUnreadCount(Long userId) {
        return Map.of("count", notificationRepository.countByUserIdAndReadFalse(userId));
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, Integer.MAX_VALUE))
                .forEach(n -> {
                    n.setRead(true);
                    notificationRepository.save(n);
                });
    }

    @Transactional
    public void createNotification(User targetUser, String title, String message,
                                   Notification.NotificationType type, Long referenceId,
                                   User sender) {
        Notification notification = Notification.builder()
                .user(targetUser)
                .title(title)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .senderNickname(sender != null ? sender.getNickname() : null)
                .senderProfileImageUrl(sender != null ? sender.getProfileImageUrl() : null)
                .build();
        notificationRepository.save(notification);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .notificationId(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType().name())
                .read(n.isRead())
                .referenceId(n.getReferenceId())
                .senderNickname(n.getSenderNickname())
                .senderProfileImageUrl(n.getSenderProfileImageUrl())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
