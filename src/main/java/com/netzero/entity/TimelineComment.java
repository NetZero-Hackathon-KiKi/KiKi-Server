package com.netzero.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "timeline_comments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimelineComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private TimelinePost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String content;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
