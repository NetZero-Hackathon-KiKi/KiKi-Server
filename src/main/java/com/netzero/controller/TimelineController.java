package com.netzero.controller;

import com.netzero.dto.response.ApiResponse;
import com.netzero.dto.response.TimelinePostResponse;
import com.netzero.repository.TimelinePostRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Timeline", description = "타임라인 관련 API")
@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
public class TimelineController {

    private final TimelinePostRepository timelinePostRepository;

    @Operation(summary = "타임라인 조회", description = "전체 타임라인 게시글을 최신순으로 조회합니다.")
    @GetMapping
    @Transactional(readOnly = true)
    public ApiResponse<?> getTimeline(@Parameter(description = "유저 ID") @RequestParam Long userId) {
        List<TimelinePostResponse> posts = timelinePostRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(TimelinePostResponse::from)
                .toList();
        return ApiResponse.ok(posts);
    }
}
