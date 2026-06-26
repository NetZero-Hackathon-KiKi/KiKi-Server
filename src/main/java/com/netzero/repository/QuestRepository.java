package com.netzero.repository;

import com.netzero.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestRepository extends JpaRepository<Quest, Long> {

    List<Quest> findByType(Quest.QuestType type);
}
