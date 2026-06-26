package com.netzero.repository;

import com.netzero.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByDepartmentAndUniversityOrderByGpDesc(String department, String university);
    List<User> findByDepartmentAndUniversityOrderByXpDesc(String department, String university);
    List<User> findByUniversityOrderByXpDesc(String university);
}
