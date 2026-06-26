package com.netzero.dto.response;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class QuestVerifyResponse {
    private boolean verified;
    private String status;
    private int rewardXp;
    private int rewardGp;
    private TimelinePostInfo timelinePost;

    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class TimelinePostInfo {
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
    }
}
