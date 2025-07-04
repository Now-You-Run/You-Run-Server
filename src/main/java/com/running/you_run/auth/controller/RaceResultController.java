package com.running.you_run.auth.controller;

import com.running.you_run.auth.dto.RaceResultDto;
import com.running.you_run.service.RaceResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.running.you_run.auth.dto.RaceResultSaveDto;
import com.running.you_run.auth.service.MyPageService;
import com.running.you_run.auth.entity.RaceResult;

import java.util.List;

@RestController
@RequestMapping("/race-results")
@RequiredArgsConstructor
public class RaceResultController {

    private final RaceResultService raceResultService;
    private final MyPageService mypageService;

    /**
     * 경기 결과 등록
     */
    @PostMapping("/{userId}")
    public ResponseEntity<RaceResultDto> createRaceResult(
            @PathVariable Long userId,
            @RequestBody RaceResultSaveDto request) {
        // 서비스에서 RaceResultDto를 반환한다고 가정
        RaceResultDto created = raceResultService.createRaceResult(userId, request);
        //mypageService.updateUserAfterRun(userId, request.getDistance());
        return ResponseEntity.ok(created);
    }

    /**
     * 특정 유저의 경기 결과 리스트 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RaceResultDto>> getRaceResultsByUser(
            @PathVariable Long userId) {
        List<RaceResultDto> results = raceResultService.getRaceResultsByUser(userId);
        return ResponseEntity.ok(results);
    }

    /**
     * 경기 결과 단건 조회
     */
    @GetMapping("/{resultId}")
    public ResponseEntity<RaceResultDto> getRaceResultById(
            @PathVariable Long resultId) {
        RaceResultDto result = raceResultService.getRaceResultById(resultId);
        return ResponseEntity.ok(result);
    }

    // 레벨업 로직
    @PostMapping("/{userId}/level")
    public ResponseEntity<?> saveRaceResult(
            @PathVariable Long userId,
            @RequestBody RaceResultDto dto) {

        RaceResult raceResult = RaceResult.builder()
                .raceName(dto.getRaceName())
                .raceDate(dto.getRaceDate().atStartOfDay())
                .resultTime(dto.getResultTime())
                .pace(dto.getPace())
                .rank(dto.getRank())
                .distance(dto.getDistance())
                .averagePace(dto.getAveragePace())
                .build();

        raceResultService.saveRaceResult(userId, raceResult);

        return ResponseEntity.ok().build();
    }
}
