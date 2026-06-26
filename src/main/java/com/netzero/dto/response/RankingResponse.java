package com.netzero.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingResponse {

    private List<RankEntryResponse> rankings;
    private RankEntryResponse myRanking;
    private int rankChange;
}
