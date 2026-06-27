package com.netzero.dto.response;

import com.netzero.entity.TimelinePost;
import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class TimelinePostResponse {
    private Long postId;
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private String questTitle;
    private String imageUrl;
    private String caption;
    private int likeCount;
    private int commentCount;
    private String createdAt;

    public static TimelinePostResponse from(TimelinePost post) {
        return TimelinePostResponse.builder()
                .postId(post.getId())
                .userId(post.getUser().getId())
                .nickname(post.getUser().getNickname())
                .profileImageUrl(post.getUser().getProfileImageUrl())
                .questTitle(post.getQuestTitle())
                .imageUrl(post.getImageUrl())
                .caption(post.getCaption())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt() != null ? post.getCreatedAt().toString() : null)
                .build();
    }
}
