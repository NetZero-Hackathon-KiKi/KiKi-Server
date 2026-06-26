package com.netzero.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private Long id;
    private String nickname;
    private String email;
    private String university;
    private String department;
    private int level;
    private int currentXp;
    private int maxXp;
    private int greenPoint;
    private int friendCount;
    private String profileImageUrl;
}
