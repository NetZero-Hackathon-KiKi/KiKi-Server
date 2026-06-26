package com.netzero.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimelinePostResponse {

    private Long postId;
    private String authorNickname;
    private String authorProfileImageUrl;
    private String content;
    private String imageUrl;
    private String verificationType;
    private String verificationInfo;
    private int likeCount;
    private int commentCount;
    private boolean likedByMe;
    private LocalDateTime createdAt;
}
