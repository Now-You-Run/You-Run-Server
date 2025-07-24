package com.running.you_run.user.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageDto {

    // 사용자 기본 정보
    private Long userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private LocalDate birthDate;
    private Double height;
    private Double weight;

    // 레벨 및 경험치
    private double totalDistance;
    private int level;
    private String grade;

    // 주간 러닝 통계
    private double weeklyDistance;   // km
    private double averagePace;      // min/km
    private int runningCount;

    // 주간 기간 정보
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;

    // 최근 경기 결과
    private List<RaceResultDto> recentRaceResults;
}
