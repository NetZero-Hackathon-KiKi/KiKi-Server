package com.netzero.repository;

import com.netzero.entity.TimelineLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimelineLikeRepository extends JpaRepository<TimelineLike, Long> {

    Optional<TimelineLike> findByPostIdAndUserId(Long postId, Long userId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
