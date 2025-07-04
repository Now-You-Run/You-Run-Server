package com.running.you_run.auth.repository;

import com.running.you_run.auth.dto.RaceResultDto;
import com.running.you_run.auth.entity.RaceResult;
import com.running.you_run.auth.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.running.you_run.auth.dto.MyPageDto;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MyPageQueryRepositoryImpl implements MyPageQueryRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public MyPageDto getMyPageSummary(Long userId) {
        User user = em.find(User.class, userId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);

        // RunningRecord → RunningResult 로 변경, 필드명도 runDate 등으로 맞춤
        TypedQuery<RaceResult> rrQuery = em.createQuery(
                "SELECT r FROM RunningResult r WHERE r.user.id = :userId AND r.runDate BETWEEN :weekAgo AND :now",
                RaceResult.class);
        rrQuery.setParameter("userId", userId);
        rrQuery.setParameter("weekAgo", weekAgo);
        rrQuery.setParameter("now", now);
        List<RaceResult> records = rrQuery.getResultList();

        double totalDistance = records.stream().mapToDouble(RaceResult::getDistance).sum();
        double averagePace = records.stream().mapToDouble(RaceResult::getAveragePace).average().orElse(0.0);
        int runningCount = records.size();

        // 레이스 결과는 그대로 RaceResult 엔티티 사용
        TypedQuery<RaceResult> raceQuery = em.createQuery(
                "SELECT rr FROM RaceResult rr WHERE rr.user.id = :userId ORDER BY rr.raceDate DESC",
                RaceResult.class);
        raceQuery.setParameter("userId", userId);
        raceQuery.setMaxResults(3);
        List<RaceResult> raceResults = raceQuery.getResultList();

        List<RaceResultDto> raceResultDto = raceResults.stream()
                .map(r -> RaceResultDto.builder()
                        .raceName(r.getRaceName())
                        .raceDate(r.getRaceDate().toLocalDate())
                        .resultTime(r.getResultTime())
                        .pace(r.getPace())
                        .rank(r.getRank())
                        .build())
                .collect(Collectors.toList());

        return MyPageDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .bio(user.getBio())
                .birthDate(user.getBirthDate())
                .height(user.getHeight())
                .weight(user.getWeight())
                .level(user.getLevel())
                .experience(user.getExperience().intValue())
                .weeklyDistance(totalDistance)
                .averagePace(averagePace)
                .runningCount(runningCount)
                .recentRaceResults(raceResultDto)
                .build();
    }
}
