package com.running.you_run.auth.repository;

import com.running.you_run.auth.dto.MyPageSummaryDto;
import com.running.you_run.auth.dto.RaceResultDto;
import com.running.you_run.auth.entity.RaceResult;
import com.running.you_run.auth.entity.RunningRecord;
import com.running.you_run.auth.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MyPageQueryRepositoryImpl implements MyPageQueryRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public MyPageSummaryDto getMyPageSummary(Long userId) {
        User user = em.find(User.class, userId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);

        TypedQuery<RunningRecord> rrQuery = em.createQuery(
                "SELECT r FROM RunningRecord r WHERE r.user.id = :userId AND r.startTime BETWEEN :weekAgo AND :now",
                RunningRecord.class);
        rrQuery.setParameter("userId", userId);
        rrQuery.setParameter("weekAgo", weekAgo);
        rrQuery.setParameter("now", now);
        List<RunningRecord> records = rrQuery.getResultList();

        double totalDistance = records.stream().mapToDouble(RunningRecord::getDistance).sum();
        double averagePace = records.stream().mapToDouble(RunningRecord::getPace).average().orElse(0.0);
        int runningCount = records.size();

        TypedQuery<RaceResult> raceQuery = em.createQuery(
                "SELECT rr FROM RaceResult rr WHERE rr.user.id = :userId ORDER BY rr.raceDate DESC",
                RaceResult.class);
        raceQuery.setParameter("userId", userId);
        raceQuery.setMaxResults(3);
        List<RaceResult> raceResults = raceQuery.getResultList();

        List<RaceResultDto> raceResultDtos = raceResults.stream()
                .map(r -> RaceResultDto.builder()
                        .raceName(r.getRaceName())
                        .raceDate(r.getRaceDate())
                        .resultTime(r.getResultTime())
                        .pace(r.getPace())
                        .rank(r.getRank())
                        .build())
                .collect(Collectors.toList());

        return MyPageSummaryDto.builder()
                .name(user.getName())
                .level(user.getLevel())
                .experience(user.getExperience())
                .weeklyDistance(totalDistance)
                .averagePace(averagePace)
                .runningCount(runningCount)
                .recentRaceResults(raceResultDtos)
                .build();
    }
}
