package com.netzero.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupRankingPageResponse {

    private Long groupId;
    private String groupName;
    private String groupDescription;
    private int memberCount;
    private String leaderNickname;
    private List<GroupMemberRankResponse> rankings;
    private List<TimelinePostResponse> timelinePosts;
}
