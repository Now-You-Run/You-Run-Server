package com.running.you_run.user.repository;

import com.running.you_run.user.Enum.FriendStatus;
import com.running.you_run.user.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepository extends JpaRepository<Friend, Long> {

    // ✅ 친구 요청 대기 상태의 개수를 카운트하는 메서드 추가
    Long countByUser2IdAndStatus(Long user2Id, FriendStatus status);
}
