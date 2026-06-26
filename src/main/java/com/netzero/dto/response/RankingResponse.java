package com.netzero.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class RankingResponse {
    private List<RankEntry> rankings;
    private int myRank;

    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class RankEntry {
        private int rank;
        private Long userId;
        private String nickname;
        private String department;
        private String profileImageUrl;
        private int xp;
    }
}
