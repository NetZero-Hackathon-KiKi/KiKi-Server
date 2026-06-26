package com.netzero.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendListResponse {

    private int friendCount;
    private int friendRequestCount;
    private List<FriendResponse> friends;
}
