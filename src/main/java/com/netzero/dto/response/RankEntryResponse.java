package com.netzero.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankEntryResponse {

    private int rank;
    private Long userId;
    private String nickname;
    private String university;
    private String department;
    private String profileImageUrl;
    private int greenPoint;
    private int rankChange;
}
