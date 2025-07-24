package com.running.you_run.user.repository;

import com.running.you_run.user.entity.FriendPointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FriendPointHistoryRepository extends JpaRepository<FriendPointHistory, Long> {
    Optional<FriendPointHistory> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    List<FriendPointHistory> findBySenderId(Long senderId);
}
