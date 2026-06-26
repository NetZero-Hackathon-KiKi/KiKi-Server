package com.netzero.repository;

import com.netzero.entity.TimelineComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimelineCommentRepository extends JpaRepository<TimelineComment, Long> {

    List<TimelineComment> findByPostIdOrderByCreatedAtAsc(Long postId);
}
