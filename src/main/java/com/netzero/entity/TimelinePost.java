package com.netzero.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "timeline_posts")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimelinePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    private String content;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private VerificationType verificationType;

    private String verificationInfo;

    @Builder.Default
    private int likeCount = 0;

    @Builder.Default
    private int commentCount = 0;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum VerificationType {
        QUEST, ATTACK
    }
}
