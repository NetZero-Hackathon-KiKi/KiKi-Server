package com.netzero.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterResponse {

    private int level;
    private int currentXp;
    private int maxXp;
    private int greenPoint;
    private List<ShopItemResponse> equippedItems;
}
