package com.netzero.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shop_items")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCategory category;

    private int price;

    public enum ItemCategory {
        HAT, CLOTHES, ACCESSORY, BACKGROUND, ETC
    }
}
