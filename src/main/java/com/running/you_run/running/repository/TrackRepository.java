package com.running.you_run.running.repository;

import com.running.you_run.running.entity.RunningTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<RunningTrack, Long> {
    @Query(value = """
            SELECT *, 
              ST_Distance(
                ST_SetSRID(ST_MakePoint(:userLon, :userLat), 4326),
                path
              ) AS distance
            FROM track
            ORDER BY distance ASC
            """, nativeQuery = true)
    List<RunningTrack> findTracksOrderByDistance(
            @Param("userLon") double userLon,
            @Param("userLat") double userLat
    );

}
