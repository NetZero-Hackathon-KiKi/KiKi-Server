package com.netzero.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeResponse {

    private UserProfileResponse profile;
    private int unreadNotificationCount;
    private AttackMissionResponse currentAttackMission;
    private List<QuestResponse> dailyQuests;
    private List<QuestResponse> campusQuests;
}
