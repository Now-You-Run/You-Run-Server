package com.running.you_run.auth.repository;

import com.running.you_run.auth.entity.RaceResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RaceResultRepository extends JpaRepository<RaceResult, Long> {

    List<RaceResult> findTop5ByUserIdOrderByRaceDateDesc(Long userId);
    List<RaceResult> findByUserId(Long userId);
}
