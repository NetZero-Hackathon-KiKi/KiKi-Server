package com.netzero.repository;

import com.netzero.entity.TimelinePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimelinePostRepository extends JpaRepository<TimelinePost, Long> {

    Page<TimelinePost> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
