package com.running.you_run.gps.repository;

import com.running.you_run.gps.entity.RunningTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<RunningTrack, Long> {
}
