package com.netzero.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class AttackRequest {
    private List<Long> targetIds;
    private Long questId;
}
