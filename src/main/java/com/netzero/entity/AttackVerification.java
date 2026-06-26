package com.netzero.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attack_verifications")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttackVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attack_id", nullable = false)
    private Attack attack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verifier_id", nullable = false)
    private User verifier;

    @Builder.Default
    private boolean approved = false;

    @Builder.Default
    private LocalDateTime verifiedAt = LocalDateTime.now();
}
