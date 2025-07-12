package com.running.you_run.running.repository;

import com.running.you_run.running.entity.RunningTrack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<RunningTrack, Long> {
    @Query(value = """
            SELECT *,
              ST_Distance(
                ST_SetSRID(ST_MakePoint(:userLon, :userLat), 4326)::geography,
                path::geography
              ) AS distance
            FROM track
            ORDER BY distance ASC
            """,
            countQuery = "SELECT count(*) FROM track",
            nativeQuery = true)
    Page<RunningTrack> findTracksOrderByDistance(
            @Param("userLon") double userLon,
            @Param("userLat") double userLat,
            Pageable pageable
    );

    @Query(value = """
            SELECT *,
              ST_Distance(
                ST_SetSRID(ST_MakePoint(:userLon, :userLat), 4326)::geography,
                path::geography
              ) AS distance
            FROM track
            WHERE user_id = :userId
            ORDER BY distance ASC
            """,
            countQuery = "SELECT count(*) FROM track WHERE user_id = :userId",
            nativeQuery = true)
    Page<RunningTrack> findUserTracksOrderByDistance(
            @Param("userLon") double userLon,
            @Param("userLat") double userLat,
            @Param("userId") long userId,
            Pageable pageable
    );


}
