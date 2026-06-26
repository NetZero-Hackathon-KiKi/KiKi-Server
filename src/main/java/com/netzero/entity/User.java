package com.netzero.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;
    private String department; // 학과
    private String university; // 대학교

    private int xp;  // 경험치 (랭킹 기준)
    private int gp;  // 게임포인트 (상점 화폐)

    private String profileImageUrl;
}
