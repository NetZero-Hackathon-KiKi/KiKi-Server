package com.netzero.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttackRequest {

    @NotNull
    private Long targetUserId;

    @NotNull
    private Long questId;

    private int gpAtStake;
}
