package com.netzero.repository;

import com.netzero.entity.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    List<UserItem> findByUserId(Long userId);
}
