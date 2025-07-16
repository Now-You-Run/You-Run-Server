package com.running.you_run.user.repository;

import com.running.you_run.user.entity.Avatar;
import com.running.you_run.user.entity.User;
import com.running.you_run.user.entity.UserAvatar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAvatarRepository extends JpaRepository<UserAvatar, Long> {
    List<UserAvatar> findByUser(User user);
    Optional<UserAvatar> findByUserAndAvatar(User user, Avatar avatar);
} 