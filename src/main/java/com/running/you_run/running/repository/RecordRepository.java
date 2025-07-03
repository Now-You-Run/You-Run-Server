package com.running.you_run.running.repository;

import com.running.you_run.running.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByTrackId(Long trackId);
    List<Record> findAllById(Long id);
    List<Record> findByUserId(Long userId);
}
