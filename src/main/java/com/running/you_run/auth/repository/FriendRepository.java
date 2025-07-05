package com.running.you_run.auth.repository;

import com.running.you_run.auth.Enum.FriendStatus;
import com.running.you_run.auth.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);
    boolean existsByUser1IdAndUser2Id(Long user1Id, Long user2Id);
    List<Friend> findAllByUser1IdAndStatus(Long userId, FriendStatus status);
}
