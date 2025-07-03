package com.running.you_run.running.repository;

import com.running.you_run.running.entity.RunningTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<RunningTrack, Long> {
}
