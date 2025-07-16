package com.running.you_run.user.repository;

import com.running.you_run.user.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
} 