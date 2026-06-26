package com.netzero.repository;

import com.netzero.entity.AttackVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttackVerificationRepository extends JpaRepository<AttackVerification, Long> {

    int countByAttackIdAndApproved(Long attackId, boolean approved);

    boolean existsByAttackIdAndVerifierId(Long attackId, Long verifierId);
}
