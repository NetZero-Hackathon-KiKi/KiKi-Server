package com.netzero.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attacks")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attacker_id", nullable = false)
    private User attacker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "defender_id", nullable = false)
    private User defender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_quest_id", nullable = false)
    private Quest missionQuest;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AttackStatus status = AttackStatus.PENDING;

    private String verificationImageUrl;

    private String verificationContent;

    @Builder.Default
    private int groupVerificationCount = 0;

    @Builder.Default
    private int requiredGroupVerification = 2;

    private int gpAtStake;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @OneToMany(mappedBy = "attack", cascade = CascadeType.ALL)
    private List<AttackVerification> verifications = new ArrayList<>();

    public enum AttackStatus {
        PENDING, VERIFIED, GROUP_VERIFIED, DEFENDED, FAILED
    }
}
