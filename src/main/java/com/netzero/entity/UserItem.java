package com.netzero.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_items")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ShopItem item;

    @Builder.Default
    private boolean equipped = false;

    @Builder.Default
    private LocalDateTime purchasedAt = LocalDateTime.now();
}
