package com.netzero.repository;

import com.netzero.entity.UserQuest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserQuestRepository extends JpaRepository<UserQuest, Long> {
    List<UserQuest> findByUserIdAndStatus(Long userId, String status);
}
