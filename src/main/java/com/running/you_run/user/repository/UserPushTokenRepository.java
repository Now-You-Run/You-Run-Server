package com.running.you_run.user.repository;

import com.running.you_run.user.entity.UserPushToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPushTokenRepository extends JpaRepository<UserPushToken, Long> {
    Optional<UserPushToken> findByUserId(Long userId);
}