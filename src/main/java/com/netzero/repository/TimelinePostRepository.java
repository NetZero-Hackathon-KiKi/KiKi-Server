package com.netzero.repository;

import com.netzero.entity.TimelinePost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimelinePostRepository extends JpaRepository<TimelinePost, Long> {
    List<TimelinePost> findAllByOrderByCreatedAtDesc();
    List<TimelinePost> findByUser_DepartmentAndUser_UniversityOrderByCreatedAtDesc(String department, String university);
}
