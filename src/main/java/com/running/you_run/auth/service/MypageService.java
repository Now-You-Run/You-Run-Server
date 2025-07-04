package com.running.you_run.auth.service;

import com.running.you_run.auth.dto.MyPageDto;
import com.running.you_run.auth.entity.User;
import com.running.you_run.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import com.running.you_run.auth.repository.MyPageQueryRepository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.PersistenceContext;
import com.running.you_run.auth.entity.RaceResult;
import com.running.you_run.auth.dto.RaceResultDto;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final MyPageQueryRepository myPageQueryRepository;

    @PersistenceContext
    private EntityManager em;

    public MyPageDto getUserProfile(Long userId) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);

        return MyPageDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .birthDate(user.getBirthDate())
                .height(user.getHeight())
                .weight(user.getWeight())
                .build();
    }

    public MyPageDto updateUserProfile(Long userId, MyPageDto updateRequest) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);

        user.setNickname(updateRequest.getNickname());
        user.setBio(updateRequest.getBio());
        user.setProfileImageUrl(updateRequest.getProfileImageUrl());
        user.setBirthDate(updateRequest.getBirthDate());
        user.setHeight(updateRequest.getHeight());
        user.setWeight(updateRequest.getWeight());

        userRepository.save(user);

        return MyPageDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .birthDate(user.getBirthDate())
                .height(user.getHeight())
                .weight(user.getWeight())
                .build();
    }

    @Transactional
    public MyPageDto createUserProfile(MyPageDto request) {
        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .bio(request.getBio())
                .profileImageUrl(request.getProfileImageUrl())
                .birthDate(request.getBirthDate())
                .height(request.getHeight())
                .weight(request.getWeight())
                .build();

        User savedUser = userRepository.save(user);

        return MyPageDto.builder()
                .userId(user.getId())
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .bio(savedUser.getBio())
                .profileImageUrl(savedUser.getProfileImageUrl())
                .birthDate(savedUser.getBirthDate())
                .height(savedUser.getHeight())
                .weight(savedUser.getWeight())
                .build();
    }

    @Transactional(readOnly = true)
    public MyPageDto getMyPageSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);

        TypedQuery<RaceResult> weeklyRunsQuery = em.createQuery(
                "SELECT r FROM RaceResult r WHERE r.user.id = :userId AND r.runDate BETWEEN :weekAgo AND :now",
                RaceResult.class);
        weeklyRunsQuery.setParameter("userId", userId);
        weeklyRunsQuery.setParameter("weekAgo", weekAgo);
        weeklyRunsQuery.setParameter("now", now);
        List<RaceResult> weeklyRuns = weeklyRunsQuery.getResultList();

        double weeklyDistance = weeklyRuns.stream().mapToDouble(RaceResult::getDistance).sum();
        double averagePace = weeklyRuns.stream().mapToDouble(RaceResult::getAveragePace).average().orElse(0.0);
        int runningCount = weeklyRuns.size();

        TypedQuery<RaceResult> recentRacesQuery = em.createQuery(
                "SELECT r FROM RaceResult r WHERE r.user.id = :userId ORDER BY r.runDate DESC",
                RaceResult.class);
        recentRacesQuery.setParameter("userId", userId);
        recentRacesQuery.setMaxResults(3);
        List<RaceResult> recentRaces = recentRacesQuery.getResultList();

        List<RaceResultDto> recentRaceResults = recentRaces.stream()
                .map(r -> RaceResultDto.builder()
                        .id(r.getId())
                        .raceName(r.getRaceName())
                        .raceDate(r.getRaceDate().toLocalDate())
                        .resultTime(r.getResultTime())
                        .pace(r.getAveragePace())
                        .rank(r.getRank())
                        .build())
                .collect(Collectors.toList());

        LocalDate weekStartDate = LocalDate.now().minusDays(6);
        LocalDate weekEndDate = LocalDate.now();

        return MyPageDto.builder()
                .userId(user.getId())
                .level(user.getLevel())
                .experience(user.getExperience().intValue())
                .weeklyDistance(weeklyDistance)
                .averagePace(averagePace)
                .runningCount(runningCount)
                .weekStartDate(weekStartDate)
                .weekEndDate(weekEndDate)
                .recentRaceResults(recentRaceResults)
                .build();
    }

    @Transactional
    public void addExperienceAndLevelUp(Long userId, int exp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        double newExp = user.getExperience() + exp;
        int newLevel = user.getLevel();

        // 예시: 경험치 100 이상이면 레벨업
        while (newExp >= 100) {
            newExp -= 100;
            newLevel += 1;
        }

        user.setExperience(newExp);
        user.setLevel(newLevel);

        userRepository.save(user);
    }



}
