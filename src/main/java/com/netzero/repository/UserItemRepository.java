package com.netzero.repository;

import com.netzero.entity.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {

    List<UserItem> findByUserId(Long userId);

    List<UserItem> findByUserIdAndEquipped(Long userId, boolean equipped);

    Optional<UserItem> findByUserIdAndItemId(Long userId, Long itemId);

    boolean existsByUserIdAndItemId(Long userId, Long itemId);
}
