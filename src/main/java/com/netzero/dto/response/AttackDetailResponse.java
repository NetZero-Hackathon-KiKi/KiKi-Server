package com.netzero.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttackDetailResponse {

    private Long attackId;
    private String defenderNickname;
    private String defenderProfileImageUrl;
    private String questTitle;
    private String questDescription;
    private int rewardGp;
    private int rewardXp;
    private String verificationImageUrl;
    private String verificationContent;
    private String status;
    private int gpAtStake;
    private int currentVerificationCount;
    private int requiredVerificationCount;
    private List<VerifierInfo> verifiers;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VerifierInfo {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
        private boolean approved;
    }
}
