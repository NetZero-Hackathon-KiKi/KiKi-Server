package com.netzero.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberRankResponse {

    private int rank;
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private int greenPoint;
}
