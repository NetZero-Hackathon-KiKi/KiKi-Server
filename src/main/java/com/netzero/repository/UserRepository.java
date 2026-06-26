package com.netzero.repository;

import com.netzero.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    @Query("SELECT u FROM User u WHERE u.university = :university ORDER BY u.currentXp DESC")
    Page<User> findByUniversityOrderByCurrentXpDesc(@Param("university") String university, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.university = :university AND u.department = :department ORDER BY u.currentXp DESC")
    Page<User> findByUniversityAndDepartmentOrderByCurrentXpDesc(
            @Param("university") String university, @Param("department") String department, Pageable pageable);
    
    List<User> findByNicknameContaining(String keyword);
}
