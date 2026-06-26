package com.netzero.service;

import com.netzero.dto.request.CreateGroupRequest;
import com.netzero.dto.request.TimelineCommentRequest;
import com.netzero.dto.response.*;
import com.netzero.entity.*;
import com.netzero.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final TimelinePostRepository timelinePostRepository;
    private final TimelineLikeRepository timelineLikeRepository;
    private final TimelineCommentRepository timelineCommentRepository;

    @Transactional
    public Map<String, Object> createGroup(Long userId, CreateGroupRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Group group = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .leader(user)
                .build();
        Group saved = groupRepository.save(group);

        user.setGroup(saved);
        userRepository.save(user);

        return Map.of("groupId", saved.getId(), "name", saved.getName());
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        return GroupResponse.builder()
                .groupId(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .memberCount(group.getMembers().size())
                .leaderNickname(group.getLeader() != null ? group.getLeader().getNickname() : null)
                .build();
    }

    @Transactional(readOnly = true)
    public List<GroupMemberRankResponse> getGroupRanking(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        AtomicInteger rank = new AtomicInteger(1);
        return group.getMembers().stream()
                .sorted(Comparator.comparingInt(User::getGreenPoint).reversed())
                .map(member -> GroupMemberRankResponse.builder()
                        .rank(rank.getAndIncrement())
                        .userId(member.getId())
                        .nickname(member.getNickname())
                        .profileImageUrl(member.getProfileImageUrl())
                        .greenPoint(member.getGreenPoint())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GroupRankingPageResponse getGroupRankingPage(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getGroup() == null) {
            return null;
        }

        Group group = user.getGroup();
        List<GroupMemberRankResponse> rankings = getGroupRanking(group.getId());

        List<TimelinePostResponse> timeline = timelinePostRepository
                .findByGroupIdOrderByCreatedAtDesc(group.getId(), PageRequest.of(page, size))
                .stream()
                .map(post -> toTimelinePostResponse(post, userId))
                .collect(Collectors.toList());

        return GroupRankingPageResponse.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .groupDescription(group.getDescription())
                .memberCount(group.getMembers().size())
                .leaderNickname(group.getLeader() != null ? group.getLeader().getNickname() : null)
                .rankings(rankings)
                .timelinePosts(timeline)
                .build();
    }

    @Transactional(readOnly = true)
    public List<TimelinePostResponse> getTimeline(Long groupId, Long userId, int page, int size) {
        return timelinePostRepository.findByGroupIdOrderByCreatedAtDesc(groupId, PageRequest.of(page, size))
                .stream()
                .map(post -> toTimelinePostResponse(post, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void joinGroup(Long userId, Long groupId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        user.setGroup(group);
        userRepository.save(user);
    }

    @Transactional
    public void leaveGroup(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.setGroup(null);
        userRepository.save(user);
    }

    @Transactional
    public Map<String, Object> toggleLike(Long userId, Long postId) {
        TimelinePost post = timelinePostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return timelineLikeRepository.findByPostIdAndUserId(postId, userId)
                .map(like -> {
                    timelineLikeRepository.delete(like);
                    post.setLikeCount(post.getLikeCount() - 1);
                    timelinePostRepository.save(post);
                    return Map.<String, Object>of("liked", false, "likeCount", post.getLikeCount());
                })
                .orElseGet(() -> {
                    TimelineLike newLike = TimelineLike.builder().post(post).user(user).build();
                    timelineLikeRepository.save(newLike);
                    post.setLikeCount(post.getLikeCount() + 1);
                    timelinePostRepository.save(post);
                    return Map.<String, Object>of("liked", true, "likeCount", post.getLikeCount());
                });
    }

    @Transactional
    public Map<String, Object> addComment(Long userId, Long postId, TimelineCommentRequest request) {
        TimelinePost post = timelinePostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        TimelineComment comment = TimelineComment.builder()
                .post(post)
                .author(user)
                .content(request.getContent())
                .build();
        TimelineComment saved = timelineCommentRepository.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);
        timelinePostRepository.save(post);

        return Map.of("commentId", saved.getId(), "commentCount", post.getCommentCount());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getComments(Long postId) {
        return timelineCommentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(c -> Map.<String, Object>of(
                        "commentId", c.getId(),
                        "nickname", c.getAuthor().getNickname(),
                        "profileImageUrl", c.getAuthor().getProfileImageUrl() != null
                                ? c.getAuthor().getProfileImageUrl() : "",
                        "content", c.getContent(),
                        "createdAt", c.getCreatedAt().toString()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GroupResponse getMyGroup(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (user.getGroup() == null) return null;
        return getGroup(user.getGroup().getId());
    }

    private TimelinePostResponse toTimelinePostResponse(TimelinePost post, Long userId) {
        return TimelinePostResponse.builder()
                .postId(post.getId())
                .authorNickname(post.getAuthor().getNickname())
                .authorProfileImageUrl(post.getAuthor().getProfileImageUrl())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .verificationType(post.getVerificationType() != null ? post.getVerificationType().name() : null)
                .verificationInfo(post.getVerificationInfo())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .likedByMe(timelineLikeRepository.existsByPostIdAndUserId(post.getId(), userId))
                .createdAt(post.getCreatedAt())
                .build();
    }
}
