package com.netzero.controller;

import com.netzero.config.AuthUtil;
import com.netzero.dto.response.ApiResponse;
import com.netzero.dto.response.FriendListResponse;
import com.netzero.dto.response.FriendResponse;
import com.netzero.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "친구", description = "친구 목록, 요청/수락/거절, 검색, 삭제 API")
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "친구 목록", description = "수락된 친구 목록을 반환합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<FriendResponse>>> getFriends() {
        return ResponseEntity.ok(ApiResponse.ok(friendService.getFriends(AuthUtil.getCurrentUserId())));
    }

    @Operation(summary = "친구 화면 통합 데이터", description = "친구 수, 친구 요청 수, 친구 목록을 한 번에 반환합니다.")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<FriendListResponse>> getFriendListData() {
        return ResponseEntity.ok(ApiResponse.ok(friendService.getFriendListData(AuthUtil.getCurrentUserId())));
    }

    @Operation(summary = "친구 요청 보내기", description = "대상 사용자에게 친구 요청을 보냅니다.")
    @PostMapping("/request/{targetUserId}")
    public ResponseEntity<ApiResponse<Void>> sendFriendRequest(
            @Parameter(description = "요청 대상 사용자 ID") @PathVariable Long targetUserId) {
        friendService.sendFriendRequest(AuthUtil.getCurrentUserId(), targetUserId);
        return ResponseEntity.ok(ApiResponse.ok("친구 요청을 보냈습니다.", null));
    }

    @Operation(summary = "받은 친구 요청 목록", description = "대기 중인 친구 요청 목록을 반환합니다.")
    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<FriendResponse>>> getFriendRequests() {
        return ResponseEntity.ok(ApiResponse.ok(friendService.getFriendRequests(AuthUtil.getCurrentUserId())));
    }

    @Operation(summary = "친구 요청 수락", description = "받은 친구 요청을 수락합니다.")
    @PutMapping("/request/{friendshipId}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptFriendRequest(
            @Parameter(description = "친구 관계 ID") @PathVariable Long friendshipId) {
        friendService.acceptFriendRequest(friendshipId);
        return ResponseEntity.ok(ApiResponse.ok("친구 요청을 수락했습니다.", null));
    }

    @Operation(summary = "친구 요청 거절", description = "받은 친구 요청을 거절합니다.")
    @PutMapping("/request/{friendshipId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectFriendRequest(
            @Parameter(description = "친구 관계 ID") @PathVariable Long friendshipId) {
        friendService.rejectFriendRequest(friendshipId);
        return ResponseEntity.ok(ApiResponse.ok("친구 요청을 거절했습니다.", null));
    }

    @Operation(summary = "친구 삭제", description = "친구 관계를 삭제합니다.")
    @DeleteMapping("/{friendId}")
    public ResponseEntity<ApiResponse<Void>> removeFriend(
            @Parameter(description = "삭제할 친구 사용자 ID") @PathVariable Long friendId) {
        friendService.removeFriend(AuthUtil.getCurrentUserId(), friendId);
        return ResponseEntity.ok(ApiResponse.ok("친구가 삭제되었습니다.", null));
    }

    @Operation(summary = "사용자 검색", description = "닉네임으로 사용자를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> searchUsers(
            @Parameter(description = "검색 키워드") @RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.ok(friendService.searchUsers(keyword)));
    }
}
