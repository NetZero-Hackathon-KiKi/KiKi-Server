package com.netzero.controller;

import com.netzero.dto.response.ApiResponse;
import com.netzero.repository.TimelinePostRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Timeline", description = "타임라인 관련 API")
@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
public class TimelineController {

    private final TimelinePostRepository timelinePostRepository;

    @Operation(summary = "타임라인 조회", description = "전체 타임라인 게시글을 최신순으로 조회합니다.")
    @GetMapping
    public ApiResponse<?> getTimeline() {
        return ApiResponse.ok(timelinePostRepository.findAllByOrderByCreatedAtDesc());
    }
}
