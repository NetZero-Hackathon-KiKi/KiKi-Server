package com.netzero.repository;

import com.netzero.entity.Attack;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttackRepository extends JpaRepository<Attack, Long> {
    List<Attack> findByTargetIdOrderByCreatedAtDesc(Long targetId);
    List<Attack> findByAttackerIdOrderByCreatedAtDesc(Long attackerId);
    List<Attack> findByTargetIdAndCompletedFalseOrderByCreatedAtDesc(Long targetId);
}
