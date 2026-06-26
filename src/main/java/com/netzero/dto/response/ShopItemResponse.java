package com.netzero.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopItemResponse {

    private Long itemId;
    private String name;
    private String description;
    private String imageUrl;
    private String category;
    private int price;
    private boolean owned;
    private boolean equipped;
}
