package com.netzero.repository;

import com.netzero.entity.Attack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttackRepository extends JpaRepository<Attack, Long> {

    List<Attack> findByDefenderIdAndStatus(Long defenderId, Attack.AttackStatus status);

    List<Attack> findByDefenderIdOrderByCreatedAtDesc(Long defenderId);

    List<Attack> findByAttackerIdOrderByCreatedAtDesc(Long attackerId);
}
