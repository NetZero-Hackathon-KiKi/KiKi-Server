package com.netzero.repository;

import com.netzero.entity.UserQuest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserQuestRepository extends JpaRepository<UserQuest, Long> {

    List<UserQuest> findByUserIdAndAssignedDate(Long userId, LocalDate date);

    Optional<UserQuest> findByUserIdAndQuestIdAndAssignedDate(Long userId, Long questId, LocalDate date);
}
