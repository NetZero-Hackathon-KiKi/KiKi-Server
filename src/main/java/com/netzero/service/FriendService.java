package com.netzero.service;

import com.netzero.dto.response.FriendListResponse;
import com.netzero.dto.response.FriendResponse;
import com.netzero.entity.Friendship;
import com.netzero.entity.Notification;
import com.netzero.entity.User;
import com.netzero.repository.FriendshipRepository;
import com.netzero.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<FriendResponse> getFriends(Long userId) {
        return friendshipRepository.findAcceptedFriendships(userId).stream()
                .map(f -> {
                    User friend = f.getRequester().getId().equals(userId)
                            ? f.getReceiver() : f.getRequester();
                    return toResponse(f.getId(), friend, "ACCEPTED");
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FriendListResponse getFriendListData(Long userId) {
        List<FriendResponse> friends = getFriends(userId);
        int requestCount = friendshipRepository.findByReceiverIdAndStatus(
                userId, Friendship.FriendshipStatus.PENDING).size();

        return FriendListResponse.builder()
                .friendCount(friends.size())
                .friendRequestCount(requestCount)
                .friends(friends)
                .build();
    }

    @Transactional
    public void sendFriendRequest(Long requesterId, Long receiverId) {
        if (requesterId.equals(receiverId)) {
            throw new IllegalArgumentException("자신에게 친구 요청을 보낼 수 없습니다.");
        }
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("대상 사용자를 찾을 수 없습니다."));

        Friendship friendship = Friendship.builder()
                .requester(requester)
                .receiver(receiver)
                .build();
        friendshipRepository.save(friendship);

        notificationService.createNotification(
                receiver,
                "친구 요청이 도착했어요!",
                requester.getNickname() + "님이 친구 요청을 보냈어요.",
                Notification.NotificationType.FRIEND,
                friendship.getId(),
                requester
        );
    }

    @Transactional(readOnly = true)
    public List<FriendResponse> getFriendRequests(Long userId) {
        return friendshipRepository.findByReceiverIdAndStatus(userId, Friendship.FriendshipStatus.PENDING)
                .stream()
                .map(f -> toResponse(f.getId(), f.getRequester(), "PENDING"))
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptFriendRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청을 찾을 수 없습니다."));
        friendship.setStatus(Friendship.FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);

        notificationService.createNotification(
                friendship.getRequester(),
                "친구 요청이 수락됐어요!",
                friendship.getReceiver().getNickname() + "님이 친구 요청을 수락했어요.",
                Notification.NotificationType.FRIEND,
                friendshipId,
                friendship.getReceiver()
        );
    }

    @Transactional
    public void rejectFriendRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청을 찾을 수 없습니다."));
        friendship.setStatus(Friendship.FriendshipStatus.REJECTED);
        friendshipRepository.save(friendship);
    }

    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        friendshipRepository.findAcceptedFriendships(userId).stream()
                .filter(f -> f.getRequester().getId().equals(friendId) || f.getReceiver().getId().equals(friendId))
                .findFirst()
                .ifPresent(friendshipRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> searchUsers(String keyword) {
        return userRepository.findByNicknameContaining(keyword).stream()
                .map(u -> Map.<String, Object>of(
                        "userId", u.getId(),
                        "nickname", u.getNickname(),
                        "university", u.getUniversity() != null ? u.getUniversity() : "",
                        "department", u.getDepartment() != null ? u.getDepartment() : "",
                        "level", u.getLevel()
                ))
                .collect(Collectors.toList());
    }

    private FriendResponse toResponse(Long friendshipId, User user, String status) {
        return FriendResponse.builder()
                .friendshipId(friendshipId)
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .level(user.getLevel())
                .greenPoint(user.getGreenPoint())
                .status(status)
                .build();
    }
}
