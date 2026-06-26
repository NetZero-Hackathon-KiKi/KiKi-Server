package com.netzero.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttackMissionResponse {

    private Long attackId;
    private String attackerNickname;
    private String defenderNickname;
    private String missionTitle;
    private String missionDescription;
    private String status;
    private int gpAtStake;
    private String verificationImageUrl;
    private int groupVerificationCount;
    private int requiredGroupVerification;
    private LocalDateTime deadline;
    private long remainingSeconds;
}
