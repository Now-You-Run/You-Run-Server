//package com.running.you_run.user.service;
//
//import com.running.you_run.user.dto.MyPageDto;
//import com.running.you_run.user.dto.RaceResultDto;
//import com.running.you_run.user.entity.User;
//import com.running.you_run.user.repository.MyPageQueryRepository;
//import com.running.you_run.user.repository.UserRepository;
//import com.running.you_run.running.entity.Record;
//import jakarta.annotation.PostConstruct;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.TypedQuery;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class MyPageService {
//
//    private final UserRepository userRepository;
//    private final MyPageQueryRepository myPageQueryRepository;
//    private final Map<Integer, Double> levelDistanceMap = new HashMap<>();
//    @PersistenceContext
//    private EntityManager em;
//
//    @PostConstruct
//    private void initLevelDistanceMap() {
//        double sum = 0;
//        levelDistanceMap.put(1, 0.0);
//        for (int i = 2; i <= 1000; i++) {
//            sum += getDistanceToLevelUp(i - 1);
//            levelDistanceMap.put(i, sum);
//        }
//    }
//
//    public MyPageDto getUserProfile(Long userId) throws NotFoundException {
//        User user = userRepository.findById(userId)
//                .orElseThrow(NotFoundException::new);
//
//        int level = user.getLevel();
//        String grade = user.getGrade();
//
//        return MyPageDto.builder()
//                .userId(user.getId())
//                .email(user.getEmail())
//                .nickname(user.getNickname())
//                .profileImageUrl(user.getProfileImageUrl())
//                .birthDate(user.getBirthDate())
//                .height(user.getHeight())
//                .weight(user.getWeight())
//                .level(level)
//                .grade(grade)
//                .build();
//    }
//
//    public MyPageDto updateUserProfile(Long userId, MyPageDto updateRequest) throws NotFoundException {
//        User user = userRepository.findById(userId)
//                .orElseThrow(NotFoundException::new);
//
//        user.setNickname(updateRequest.getNickname());
//        user.setProfileImageUrl(updateRequest.getProfileImageUrl());
//        user.setBirthDate(updateRequest.getBirthDate());
//        user.setHeight(updateRequest.getHeight());
//        user.setWeight(updateRequest.getWeight());
//
//        userRepository.save(user);
//
//        return MyPageDto.builder()
//                .userId(user.getId())
//                .email(user.getEmail())
//                .nickname(user.getNickname())
//                .profileImageUrl(user.getProfileImageUrl())
//                .birthDate(user.getBirthDate())
//                .height(user.getHeight())
//                .weight(user.getWeight())
//                .build();
//    }
//
//    @Transactional
//    public MyPageDto createUserProfile(MyPageDto request) {
//        User user = User.builder()
//                .email(request.getEmail())
//                .nickname(request.getNickname())
//                .profileImageUrl(request.getProfileImageUrl())
//                .birthDate(request.getBirthDate())
//                .height(request.getHeight())
//                .weight(request.getWeight())
//                .build();
//
//        User savedUser = userRepository.save(user);
//
//        return MyPageDto.builder()
//                .userId(user.getId())
//                .email(savedUser.getEmail())
//                .nickname(savedUser.getNickname())
//                .profileImageUrl(savedUser.getProfileImageUrl())
//                .birthDate(savedUser.getBirthDate())
//                .height(savedUser.getHeight())
//                .weight(savedUser.getWeight())
//                .build();
//    }
//
//    @Transactional(readOnly = true)
//    public MyPageDto getMyPageSummary(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime weekAgo = now.minusDays(7);
//
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
//        TypedQuery<Record> recentRunsQuery = em.createQuery(
//                "SELECT r FROM Record r WHERE r.userId = :userId ORDER BY r.startedAt DESC",
//                Record.class);
//        recentRunsQuery.setParameter("userId", userId);
//        recentRunsQuery.setMaxResults(3);
//        List<Record> recentRuns = recentRunsQuery.getResultList();
//
//        List<RaceResultDto> recentRaceResults = recentRuns.stream()
//                .map(r -> RaceResultDto.builder()
//                        .id(r.getId())
//                        .raceName(r.getMode().name()) // 혹은 trackId 등
//                        .raceDate(r.getStartedAt().toLocalDate())
//                        .resultTime(r.getResultTime())
//                        .pace(r.getAveragePace())
//                        .build())
//                .collect(Collectors.toList());
//
//        LocalDate weekStartDate = LocalDate.now().minusDays(6);
//        LocalDate weekEndDate = LocalDate.now();
//
//        return MyPageDto.builder()
//                .userId(user.getId())
//                .email(user.getEmail())
//                .nickname(user.getNickname())
//                .profileImageUrl(user.getProfileImageUrl())
//                .birthDate(user.getBirthDate())
//                .height(user.getHeight())
//                .weight(user.getWeight())
//                .level(user.getLevel())
//                .grade(user.getGrade())
//                .totalDistance(user.getTotalDistance())
//                .weeklyDistance(weeklyDistance)
//                .averagePace(averagePace)
//                .runningCount(runningCount)
//                .weekStartDate(weekStartDate)
//                .weekEndDate(weekEndDate)
//                .recentRaceResults(recentRaceResults)
//                .build();
//    }
//
//
//    @Transactional
//    public void addDistanceAndLevelUp(Long userId, double distanceKm) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        double totalDistance = (user.getTotalDistance() == null ? 0.0 : user.getTotalDistance()) + distanceKm;
//        int newLevel = calculateLevelByTotalDistanceBinarySearch(totalDistance);
//
//        System.out.println("==== Level Calculation Debug ====");
//        System.out.println("Total Distance (after run): " + totalDistance);
//        System.out.println("New Level: " + newLevel);
//        System.out.println("Required distance for next level: " + getTotalDistanceForLevel(newLevel + 1));
//        System.out.println("==============================");
//
//        user.setTotalDistance(totalDistance);
//        user.setLevel(newLevel);
//        updateGrade(user, totalDistance);
//        userRepository.save(user);
//    }
//
//    private void updateGrade(User user, double totalDistance) {
//        if (totalDistance <= 50) {
//            user.setGrade("아이언");
//        } else if (totalDistance <= 100) {
//            user.setGrade("브론즈");
//        } else if (totalDistance <= 150) {
//            user.setGrade("실버");
//        } else if (totalDistance <= 200) {
//            user.setGrade("골드");
//        } else if (totalDistance <= 250) {
//            user.setGrade("플래티넘");
//        } else if (totalDistance <= 300) {
//            user.setGrade("다이아");
//        } else if (totalDistance <= 450) {
//            user.setGrade("마스터");
//        } else if (totalDistance <= 500) {
//            user.setGrade("그랜드 마스터");
//        } else if (totalDistance <= 700) {
//            user.setGrade("레전드 러너");
//        }
//    }
//
//    private int calculateLevelByTotalDistanceBinarySearch(double totalDistance) {
//        int left = 1;
//        int right = 1000;
//
//        while (left < right) {
//            int mid = (left + right + 1) / 2;
//            double requiredDistance = getTotalDistanceForLevel(mid);
//
//            if (totalDistance >= requiredDistance) {
//                left = mid;
//            } else {
//                right = mid - 1;
//            }
//        }
//        return left;
//    }
//
//    private double getTotalDistanceForLevel(int level) {
//        return levelDistanceMap.getOrDefault(level, Double.MAX_VALUE);
//    }
//
//    private double getDistanceToLevelUp(int level) {
//        if (level >= 1 && level <= 10) {
//            return 3.0 * level;
//        } else if (level >= 11 && level <= 50) {
//            return 5.0 * level;
//        } else if (level >= 51 && level <= 100) {
//            return 10.0 * level;
//        } else {
//            return Double.MAX_VALUE;
//        }
//    }
//}
