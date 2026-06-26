package com.netzero.controller;

import com.netzero.config.AuthUtil;
import com.netzero.dto.request.CreateGroupRequest;
import com.netzero.dto.request.TimelineCommentRequest;
import com.netzero.dto.response.*;
import com.netzero.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "그룹", description = "그룹 생성/가입/탈퇴, 그룹 내 랭킹, 타임라인 API")
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "그룹 생성", description = "새로운 탄소챌린지 그룹을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> createGroup(
            @Valid @RequestBody CreateGroupRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("그룹이 생성되었습니다.",
                groupService.createGroup(AuthUtil.getCurrentUserId(), request)));
    }

    @Operation(summary = "그룹 정보 조회", description = "그룹 이름, 설명, 멤버 수, 리더 정보를 반환합니다.")
    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroup(
            @Parameter(description = "그룹 ID") @PathVariable Long groupId) {
        return ResponseEntity.ok(ApiResponse.ok(groupService.getGroup(groupId)));
    }

    @Operation(summary = "그룹 랭킹 통합 페이지", description = "그룹 정보, 멤버 랭킹, 타임라인을 한 번에 반환합니다.")
    @GetMapping("/ranking-page")
    public ResponseEntity<ApiResponse<GroupRankingPageResponse>> getGroupRankingPage(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                groupService.getGroupRankingPage(AuthUtil.getCurrentUserId(), page, size)));
    }

    @Operation(summary = "그룹 내 랭킹", description = "그룹 멤버들의 GP 기준 랭킹을 반환합니다.")
    @GetMapping("/{groupId}/ranking")
    public ResponseEntity<ApiResponse<List<GroupMemberRankResponse>>> getGroupRanking(
            @Parameter(description = "그룹 ID") @PathVariable Long groupId) {
        return ResponseEntity.ok(ApiResponse.ok(groupService.getGroupRanking(groupId)));
    }

    @Operation(summary = "그룹 타임라인", description = "그룹 멤버들의 인증 게시글 타임라인을 반환합니다.")
    @GetMapping("/{groupId}/timeline")
    public ResponseEntity<ApiResponse<List<TimelinePostResponse>>> getTimeline(
            @Parameter(description = "그룹 ID") @PathVariable Long groupId,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                groupService.getTimeline(groupId, AuthUtil.getCurrentUserId(), page, size)));
    }

    @Operation(summary = "그룹 가입", description = "해당 그룹에 가입합니다.")
    @PostMapping("/{groupId}/join")
    public ResponseEntity<ApiResponse<Void>> joinGroup(
            @Parameter(description = "그룹 ID") @PathVariable Long groupId) {
        groupService.joinGroup(AuthUtil.getCurrentUserId(), groupId);
        return ResponseEntity.ok(ApiResponse.ok("그룹에 가입되었습니다.", null));
    }

    @Operation(summary = "그룹 탈퇴", description = "해당 그룹에서 탈퇴합니다.")
    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveGroup(
            @Parameter(description = "그룹 ID") @PathVariable Long groupId) {
        groupService.leaveGroup(AuthUtil.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.ok("그룹에서 탈퇴했습니다.", null));
    }

    @Operation(summary = "좋아요 토글", description = "타임라인 게시글에 좋아요를 누르거나 취소합니다.")
    @PostMapping("/{groupId}/timeline/{postId}/like")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleLike(
            @Parameter(description = "그룹 ID") @PathVariable Long groupId,
            @Parameter(description = "게시글 ID") @PathVariable Long postId) {
        return ResponseEntity.ok(ApiResponse.ok(groupService.toggleLike(AuthUtil.getCurrentUserId(), postId)));
    }

    @Operation(summary = "댓글 작성", description = "타임라인 게시글에 댓글을 작성합니다.")
    @PostMapping("/{groupId}/timeline/{postId}/comments")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addComment(
            @Parameter(description = "그룹 ID") @PathVariable Long groupId,
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @Valid @RequestBody TimelineCommentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("댓글이 등록되었습니다.",
                groupService.addComment(AuthUtil.getCurrentUserId(), postId, request)));
    }

    @Operation(summary = "댓글 목록 조회", description = "타임라인 게시글의 댓글 목록을 반환합니다.")
    @GetMapping("/{groupId}/timeline/{postId}/comments")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getComments(
            @Parameter(description = "그룹 ID") @PathVariable Long groupId,
            @Parameter(description = "게시글 ID") @PathVariable Long postId) {
        return ResponseEntity.ok(ApiResponse.ok(groupService.getComments(postId)));
    }

    @Operation(summary = "내 그룹 정보", description = "현재 소속 그룹 정보를 반환합니다. 없으면 null.")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<GroupResponse>> getMyGroup() {
        return ResponseEntity.ok(ApiResponse.ok(groupService.getMyGroup(AuthUtil.getCurrentUserId())));
    }
}
