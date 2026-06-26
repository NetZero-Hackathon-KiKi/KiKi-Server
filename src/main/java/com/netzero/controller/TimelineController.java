package com.netzero.controller;

import com.netzero.dto.response.ApiResponse;
import com.netzero.repository.TimelinePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
public class TimelineController {

    private final TimelinePostRepository timelinePostRepository;

    // 타임라인 전체 목록 (최신순)
    @GetMapping
    public ApiResponse<?> getTimeline() {
        return ApiResponse.ok(timelinePostRepository.findAllByOrderByCreatedAtDesc());
    }
}
