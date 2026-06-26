package com.netzero.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttackTargetResponse {

    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private int greenPoint;
    private int level;
}
