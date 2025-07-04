package com.running.you_run.service;

import com.running.you_run.auth.dto.RaceResultDto;
import com.running.you_run.auth.entity.RaceResult;
import com.running.you_run.auth.entity.User;
import com.running.you_run.auth.repository.RaceResultRepository;
import com.running.you_run.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.running.you_run.auth.dto.RaceResultSaveDto;
import com.running.you_run.auth.service.MyPageService;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;

@Service
@RequiredArgsConstructor
public class RaceResultService {

    private final RaceResultRepository raceResultRepository;
    private final UserRepository userRepository;
    private final MyPageService mypageService;

    /**
     * 경기 결과 등록
     */
    @Transactional
    public RaceResultDto createRaceResult(Long userId, RaceResultSaveDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        RaceResult raceResult = RaceResult.builder()
                .user(user)
                .raceName(request.getRaceName())
                .raceDate(request.getRaceDate().atStartOfDay())
                .resultTime(request.getResultTime())
                .pace(request.getPace())
                .rank(request.getRank())
                .distance(request.getDistance())
                .averagePace(request.getAveragePace())
                .build();

        RaceResult savedResult = raceResultRepository.save(raceResult);

        double distanceKm = request.getDistance();
        double experienceGained = distanceKm * 10; // 1km = 10exp

        // null 방지 초기화
        if (user.getTotalDistance() == null) user.setTotalDistance(0.0);
        if (user.getExperience() == null) user.setExperience(0.0);
        if (user.getTotalExperience() == null) user.setTotalExperience(0.0);
        if (user.getLevel() == null) user.setLevel(1);
        if (user.getGrade() == null) user.setGrade("Beginner");

        user.setTotalDistance(user.getTotalDistance() + distanceKm);
        user.setExperience(user.getExperience() + experienceGained);
        user.setTotalExperience(user.getTotalExperience() + experienceGained);

        // 레벨업 처리
        int expForNextLevel = user.getLevel() * 100;
        while (user.getExperience() >= expForNextLevel) {
            user.setExperience(user.getExperience() - expForNextLevel);
            user.setLevel(user.getLevel() + 1);
            expForNextLevel = user.getLevel() * 100;
        }

        // 등급 갱신
        int level = user.getLevel();
        if (level <= 5) user.setGrade("Beginner");
        else if (level <= 10) user.setGrade("Intermediate");
        else if (level <= 15) user.setGrade("Advanced");
        else user.setGrade("Elite");

        // User 업데이트
        userRepository.save(user);

        return RaceResultDto.builder()
                .id(savedResult.getId())
                .raceName(savedResult.getRaceName())
                .raceDate(savedResult.getRaceDate().toLocalDate())
                .resultTime(savedResult.getResultTime())
                .pace(savedResult.getPace())
                .rank(savedResult.getRank())
                .distance(savedResult.getDistance())
                .averagePace(savedResult.getAveragePace())
                .build();
    }


    /**
     * 특정 유저의 경기 결과 리스트 조회
     */
    @Transactional(readOnly = true)
    public List<RaceResultDto> getRaceResultsByUser(Long userId) {
        List<RaceResult> results = raceResultRepository.findByUserId(userId);

        return results.stream()
                .map(r -> RaceResultDto.builder()
                        .id(r.getId())
                        .raceName(r.getRaceName())
                        .raceDate(r.getRaceDate().toLocalDate())
                        .resultTime(r.getResultTime())
                        .pace(r.getPace())
                        .rank(r.getRank())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 경기 결과 단건 조회
     */
    @Transactional(readOnly = true)
    public RaceResultDto getRaceResultById(Long resultId) {
        RaceResult result = raceResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Race result not found"));

        return RaceResultDto.builder()
                .id(result.getId())
                .raceName(result.getRaceName())
                .raceDate(result.getRaceDate().toLocalDate())
                .resultTime(result.getResultTime())
                .pace(result.getPace())
                .rank(result.getRank())
                .build();
    }

    // 레벨업
    @Transactional
    public void saveRaceResult(Long userId, RaceResult raceResult) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        raceResult.setUser(user);
        raceResultRepository.save(raceResult);

        // 1) 레이스 결과 저장
        raceResultRepository.save(raceResult);

        // 2) 경험치 계산 예시 (거리 x 10)
        int gainedExp = (int)(raceResult.getDistance() * 10);

        // 3) 경험치 및 레벨업 처리
        mypageService.addExperienceAndLevelUp(userId, gainedExp);
    }


}
