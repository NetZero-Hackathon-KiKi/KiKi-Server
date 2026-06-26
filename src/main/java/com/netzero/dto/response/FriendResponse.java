package com.netzero.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendResponse {

    private Long friendshipId;
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private int level;
    private int greenPoint;
    private String status;
}
