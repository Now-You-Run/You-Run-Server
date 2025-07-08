package com.running.you_run.user.repository;

import com.running.you_run.user.Enum.FriendStatus;
import com.running.you_run.user.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);
    boolean existsByUser1IdAndUser2Id(Long user1Id, Long user2Id);
    List<Friend> findAllByUser1IdAndStatus(Long userId, FriendStatus status);
    // Find requests received by a user (they are user2) that are WAITING
    List<Friend> findAllByUser2IdAndStatus(Long user2Id, FriendStatus status);
    List<Friend> findAllByUser1IdAndStatusOrUser2IdAndStatus(Long user1Id, FriendStatus status1, Long user2Id, FriendStatus status2);

}
