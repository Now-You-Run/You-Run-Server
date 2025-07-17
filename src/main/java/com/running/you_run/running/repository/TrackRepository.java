package com.running.you_run.running.repository;

import com.running.you_run.running.entity.RunningTrack;
import com.running.you_run.user.entity.User;
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
            WHERE user_id IS NULL
            ORDER BY distance ASC
            """,
            countQuery = "SELECT count(*) FROM track",
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
            WHERE user_id = :userId
            ORDER BY distance ASC
            """,
            countQuery = "SELECT count(*) FROM track WHERE user_id = :userId",
            nativeQuery = true)
    Page<RunningTrack> findUserTracksOrderByClose(
            @Param("userLon") double userLon,
            @Param("userLat") double userLat,
            @Param("userId") long userId,
            Pageable pageable
    );

    Page<RunningTrack> findAllByUserIsNullOrderByTotalDistanceAsc(Pageable pageable);

    /**
     * 사용자가 없는(공용) 모든 트랙을 총 거리가 긴 순(내림차순)으로 정렬하여 조회합니다.
     * RunningTrack 엔티티의 user 필드가 NULL인 것을 조건으로 합니다.
     */
    Page<RunningTrack> findAllByUserIsNullOrderByTotalDistanceDesc(Pageable pageable);
    Page<RunningTrack> findByUserIdOrderByTotalDistanceAsc(Long userId, Pageable pageable);
    Page<RunningTrack> findByUserIdOrderByTotalDistanceDesc(Long userId, Pageable pageable);
}
