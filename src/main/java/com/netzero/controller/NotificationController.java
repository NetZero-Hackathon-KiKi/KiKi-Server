package com.netzero.controller;

import com.netzero.config.AuthUtil;
import com.netzero.dto.response.ApiResponse;
import com.netzero.dto.response.NotificationResponse;
import com.netzero.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "알림", description = "알림 목록 조회, 읽음 처리 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 목록", description = "전체 알림 목록을 반환합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                notificationService.getNotifications(AuthUtil.getCurrentUserId(), page, size)));
    }

    @Operation(summary = "읽지 않은 알림 수", description = "읽지 않은 알림 개수를 반환합니다.")
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getUnreadCount() {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getUnreadCount(AuthUtil.getCurrentUserId())));
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리합니다.")
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @Parameter(description = "알림 ID") @PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.ok("읽음 처리되었습니다.", null));
    }

    @Operation(summary = "전체 알림 읽음 처리", description = "모든 알림을 일괄 읽음 처리합니다.")
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        notificationService.markAllAsRead(AuthUtil.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.ok("모든 알림이 읽음 처리되었습니다.", null));
    }
}
