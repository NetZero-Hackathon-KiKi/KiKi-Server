package com.netzero.controller;

import com.netzero.dto.response.ApiResponse;
import com.netzero.dto.response.RankingResponse;
import com.netzero.entity.TimelinePost;
import com.netzero.entity.User;
import com.netzero.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Tag(name = "Ranking", description = "랭킹 관련 API")
@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @Operation(summary = "학과 랭킹 조회", description = "같은 학과 내 XP 기준 랭킹과 학과 타임라인을 조회합니다.")
    @GetMapping("/department")
    public ApiResponse<?> getDepartmentRanking(@Parameter(description = "유저 ID") @RequestParam Long userId) {
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

    @Operation(summary = "대학교 랭킹 조회", description = "같은 대학교 내 XP 기준 랭킹과 내 순위를 조회합니다.")
    @GetMapping("/university")
    public ApiResponse<RankingResponse> getUniversityRanking(@Parameter(description = "유저 ID") @RequestParam Long userId) {
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
