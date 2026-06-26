package com.netzero.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shop_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ShopItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private int price; // GP 가격
    private String imageUrl;
}
