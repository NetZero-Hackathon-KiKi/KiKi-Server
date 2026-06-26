package com.netzero.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_quests")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id", nullable = false)
    private Quest quest;

    @Builder.Default
    private int currentCount = 0;

    @Builder.Default
    private int requiredCount = 1;

    @Builder.Default
    private boolean completed = false;

    private String verificationImageUrl;

    @Column(nullable = false)
    private LocalDate assignedDate;

    private LocalDateTime completedAt;
}
