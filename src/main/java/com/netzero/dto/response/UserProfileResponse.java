package com.netzero.dto.response;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserProfileResponse {
    private Long userId;
    private String name;
    private int level;
    private int currentXp;
    private int maxXp;
    private int gp;
    private int friendCount;
    private String profileImageUrl;
}
