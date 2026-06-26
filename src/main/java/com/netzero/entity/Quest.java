package com.netzero.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quests")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Quest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String type; // DAILY, CAMPUS
    private int rewardXp;
    private int rewardGp;
}
