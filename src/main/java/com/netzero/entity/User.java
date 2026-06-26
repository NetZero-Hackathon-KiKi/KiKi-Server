package com.netzero.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    private String university;

    private String department;

    @Builder.Default
    private int level = 1;

    @Builder.Default
    private int currentXp = 0;

    @Builder.Default
    private int maxXp = 100;

    @Builder.Default
    private int greenPoint = 0;

    private String profileImageUrl;

    @Builder.Default
    private boolean attackNotification = true;

    @Builder.Default
    private boolean questNotification = true;

    @Builder.Default
    private boolean verificationNotification = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
