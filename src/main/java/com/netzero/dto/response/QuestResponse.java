package com.netzero.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestResponse {

    private Long questId;
    private String title;
    private String description;
    private String type;
    private int rewardGp;
    private int rewardXp;
    private int currentCount;
    private int requiredCount;
    private boolean completed;
    private String location;
}
