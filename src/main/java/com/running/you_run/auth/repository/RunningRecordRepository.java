package com.running.you_run.auth.repository;

import com.running.you_run.auth.entity.RunningRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface RunningRecordRepository extends JpaRepository<RunningRecord, Long> {
    List<RunningRecord> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
