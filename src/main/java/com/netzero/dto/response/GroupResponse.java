package com.netzero.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupResponse {

    private Long groupId;
    private String name;
    private String description;
    private int memberCount;
    private String leaderNickname;
    private List<GroupMemberRankResponse> rankings;
}
