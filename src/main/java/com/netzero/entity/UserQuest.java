package com.netzero.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_quests")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserQuest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id")
    private Quest quest;

    private String imageUrl;    // 인증 사진 S3 URL
    private String status;      // PENDING, SUCCESS, FAILED
    private LocalDateTime completedAt;
}
