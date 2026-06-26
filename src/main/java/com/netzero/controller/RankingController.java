package com.netzero.controller;

import com.netzero.dto.response.ApiResponse;
import com.netzero.dto.response.RankingResponse;
import com.netzero.entity.TimelinePost;
import com.netzero.entity.User;
import com.netzero.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/department")
    public ApiResponse<?> getDepartmentRanking(@RequestParam Long userId) {
        List<User> ranking = rankingService.getDepartmentRanking(userId);
        List<TimelinePost> timeline = rankingService.getDepartmentTimeline(userId);

        List<RankingResponse.RankEntry> entries = IntStream.range(0, ranking.size())
                .mapToObj(i -> RankingResponse.RankEntry.builder()
                        .rank(i + 1)
                        .userId(ranking.get(i).getId())
                        .nickname(ranking.get(i).getNickname())
                        .department(ranking.get(i).getDepartment())
                        .profileImageUrl(ranking.get(i).getProfileImageUrl())
                        .xp(ranking.get(i).getXp())
                        .build())
                .toList();

        List<Map<String, Object>> timelineData = timeline.stream()
                .map(post -> Map.<String, Object>of(
                        "postId", post.getId(),
                        "nickname", post.getUser().getNickname(),
                        "profileImageUrl", post.getUser().getProfileImageUrl() != null ? post.getUser().getProfileImageUrl() : "",
                        "questTitle", post.getQuestTitle(),
                        "imageUrl", post.getImageUrl() != null ? post.getImageUrl() : "",
                        "likeCount", post.getLikeCount(),
                        "commentCount", post.getCommentCount(),
                        "createdAt", post.getCreatedAt().toString()
                ))
                .toList();

        return ApiResponse.ok(Map.of("rankings", entries, "timeline", timelineData));
    }

    @GetMapping("/university")
    public ApiResponse<RankingResponse> getUniversityRanking(@RequestParam Long userId) {
        User me = rankingService.getUniversityRanking(userId).stream()
                .filter(u -> u.getId().equals(userId)).findFirst().orElse(null);

        List<User> ranking = rankingService.getUniversityRanking(userId);
        int myRank = rankingService.getMyRank(userId);

        List<RankingResponse.RankEntry> entries = IntStream.range(0, ranking.size())
                .mapToObj(i -> RankingResponse.RankEntry.builder()
                        .rank(i + 1)
                        .userId(ranking.get(i).getId())
                        .nickname(ranking.get(i).getNickname())
                        .department(ranking.get(i).getDepartment())
                        .profileImageUrl(ranking.get(i).getProfileImageUrl())
                        .xp(ranking.get(i).getXp())
                        .build())
                .toList();

        RankingResponse response = RankingResponse.builder()
                .rankings(entries)
                .myRank(myRank)
                .build();

        return ApiResponse.ok(response);
    }
}
