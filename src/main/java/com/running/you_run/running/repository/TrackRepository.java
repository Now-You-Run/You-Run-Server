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
            WHERE user_id IS NULL AND track_status = 'AVAILABLE'
            ORDER BY distance ASC, id ASC
            """,
            countQuery = "SELECT count(*) FROM track AND track_status = 'AVAILABLE'",
            nativeQuery = true)
    Page<RunningTrack> findTracksOrderByClose(
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
            WHERE user_id = :userId AND track_status = 'AVAILABLE'
            ORDER BY distance ASC, id ASC
            """,
            countQuery = "SELECT count(*) FROM track WHERE user_id = :userId AND track_status = 'AVAILABLE'",
            nativeQuery = true)
    Page<RunningTrack> findUserTracksOrderByClose(
            @Param("userLon") double userLon,
            @Param("userLat") double userLat,
            @Param("userId") long userId,
            Pageable pageable
    );
//    @Query("SELECT t FROM RunningTrack t WHERE t.user IS NULL " +
//            "AND t.trackStatus = com.running.you_run.running.Enum.TrackStatus.AVAILABLE ORDER BY t.totalDistance ASC")
//    Page<RunningTrack> findAllPublicAvailableTracksOrderByTotalDistanceAsc(Pageable pageable);
//    @Query("SELECT t FROM RunningTrack t WHERE t.user IS NULL " +
//            "AND t.trackStatus = com.running.you_run.running.Enum.TrackStatus.AVAILABLE ORDER BY t.totalDistance DESC")
//    Page<RunningTrack> findAllPublicAvailableTracksOrderByTotalDistanceDesc(Pageable pageable);
//    @Query("SELECT t FROM RunningTrack t WHERE t.user.id = :userId " +
//            "AND t.trackStatus = com.running.you_run.running.Enum.TrackStatus.AVAILABLE ORDER BY t.totalDistance ASC")
//    Page<RunningTrack> findUserAvailableTracksOrderByTotalDistanceAsc(Long userId, Pageable pageable);
//    @Query("SELECT t FROM RunningTrack t WHERE t.user.id = :userId " +
//            "AND t.trackStatus = com.running.you_run.running.Enum.TrackStatus.AVAILABLE ORDER BY t.totalDistance DESC")
//    Page<RunningTrack> findUserAvailableTracksOrderByTotalDistanceDesc(Long userId, Pageable pageable);



    @Query(value = "SELECT t FROM RunningTrack t WHERE t.user IS NULL AND t.trackStatus = com.running.you_run.running.Enum.TrackStatus.AVAILABLE",
            countQuery = "SELECT count(t) FROM RunningTrack t WHERE t.user IS NULL AND t.trackStatus = com.running.you_run.running.Enum.TrackStatus.AVAILABLE")
    Page<RunningTrack> findAllPublicAvailableTracks(Pageable pageable);

    @Query(value = "SELECT t FROM RunningTrack t WHERE t.user.id = :userId AND t.trackStatus = com.running.you_run.running.Enum.TrackStatus.AVAILABLE",
            countQuery = "SELECT count(t) FROM RunningTrack t WHERE t.user.id = :userId AND t.trackStatus = com.running.you_run.running.Enum.TrackStatus.AVAILABLE")
    Page<RunningTrack> findUserAvailableTracks(@Param("userId") Long userId, Pageable pageable);
}
