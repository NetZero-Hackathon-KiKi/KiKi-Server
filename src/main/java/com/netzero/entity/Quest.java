package com.netzero.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quests")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestType type;

    private int rewardGp;

    private int rewardXp;

    private String verificationMethod;

    private String location;

    public enum QuestType {
        DAILY, CAMPUS
    }
}
