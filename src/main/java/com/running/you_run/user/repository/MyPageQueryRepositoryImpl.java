//package com.running.you_run.user.repository;
//
//import com.running.you_run.user.dto.RaceResultDto;
//import com.running.you_run.user.dto.MyPageDto;
//import com.running.you_run.user.entity.User;
//import com.running.you_run.running.entity.Record;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.TypedQuery;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Repository
//@RequiredArgsConstructor
//public class MyPageQueryRepositoryImpl implements MyPageQueryRepository {
//
//    @PersistenceContext
//    private EntityManager em;
//
//    @Override
//    public MyPageDto getMyPageSummary(Long userId) {
//        User user = em.find(User.class, userId);
//        if (user == null) {
//            throw new IllegalArgumentException("User not found");
//        }
//
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime weekAgo = now.minusDays(7);
//
//        // 1. 최근 1주일간 러닝 기록
//        TypedQuery<Record> weeklyRunsQuery = em.createQuery(
//                "SELECT r FROM Record r WHERE r.userId = :userId AND r.startedAt BETWEEN :weekAgo AND :now",
//                Record.class);
//        weeklyRunsQuery.setParameter("userId", userId);
//        weeklyRunsQuery.setParameter("weekAgo", weekAgo);
//        weeklyRunsQuery.setParameter("now", now);
//        List<Record> weeklyRuns = weeklyRunsQuery.getResultList();
//
//        double weeklyDistance = weeklyRuns.stream().mapToDouble(Record::getDistance).sum();
//        double averagePace = weeklyRuns.stream().mapToDouble(Record::getAveragePace).average().orElse(0.0);
//        int runningCount = weeklyRuns.size();
//
//        // 2. 최근 3회 러닝 기록
//        TypedQuery<Record> recentRunsQuery = em.createQuery(
//                "SELECT r FROM Record r WHERE r.userId = :userId ORDER BY r.startedAt DESC",
//                Record.class);
//        recentRunsQuery.setParameter("userId", userId);
//        recentRunsQuery.setMaxResults(3);
//        List<Record> recentRuns = recentRunsQuery.getResultList();
//
//        List<RaceResultDto> raceResultDtos = recentRuns.stream()
//                .map(r -> RaceResultDto.builder()
//                        .raceName(r.getMode() != null ? r.getMode().name() : null) // mode를 이름으로 사용
//                        .raceDate(r.getStartedAt() != null ? r.getStartedAt().toLocalDate() : null)
//                        .resultTime(r.getResultTime())
//                        .pace(r.getAveragePace())
//                        .build())
//                .collect(Collectors.toList());
//
//        return MyPageDto.builder()
//                .userId(user.getId())
//                .nickname(user.getNickname())
//                .profileImageUrl(user.getProfileImageUrl())
//                .birthDate(user.getBirthDate())
//                .height(user.getHeight())
//                .weight(user.getWeight())
//                .level(user.getLevel())
//                .weeklyDistance(weeklyDistance)
//                .averagePace(averagePace)
//                .runningCount(runningCount)
//                .recentRaceResults(raceResultDtos)
//                .build();
//    }
//}
