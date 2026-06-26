package com.netzero.dto.response;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class QuestResponse {
    private Long id;
    private String title;
    private String description;
    private String type;
    private int rewardXp;
    private int rewardGp;
    private boolean completed;
}
