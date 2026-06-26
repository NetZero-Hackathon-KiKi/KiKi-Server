package com.netzero.repository;

import com.netzero.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT f FROM Friendship f WHERE (f.requester.id = :userId OR f.receiver.id = :userId) AND f.status = 'ACCEPTED'")
    List<Friendship> findAcceptedFriendships(@Param("userId") Long userId);

    List<Friendship> findByReceiverIdAndStatus(Long receiverId, Friendship.FriendshipStatus status);

    @Query("SELECT COUNT(f) FROM Friendship f WHERE (f.requester.id = :userId OR f.receiver.id = :userId) AND f.status = 'ACCEPTED'")
    int countFriends(@Param("userId") Long userId);
}
