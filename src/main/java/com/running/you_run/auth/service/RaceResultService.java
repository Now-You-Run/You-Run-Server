package com.running.you_run.auth.service;

import com.running.you_run.auth.dto.RaceResultDto;
import com.running.you_run.auth.dto.RaceResultSaveDto;
import com.running.you_run.auth.entity.RaceResult;
import com.running.you_run.auth.entity.User;
import com.running.you_run.auth.repository.RaceResultRepository;
import com.running.you_run.auth.repository.UserRepository;
import com.running.you_run.auth.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RaceResultService {

    private final RaceResultRepository raceResultRepository;
    private final UserRepository userRepository;
    private final MyPageService mypageService;

    /**
     * 경기 결과 등록 및 km 기반 레벨업 처리
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

        // MyPageService의 addDistanceAndLevelUp 메서드로 거리 누적 및 레벨업 처리
        mypageService.addDistanceAndLevelUp(userId, distanceKm);

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
                        .distance(r.getDistance())
                        .averagePace(r.getAveragePace())
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
                .distance(result.getDistance())
                .averagePace(result.getAveragePace())
                .build();
    }
}
