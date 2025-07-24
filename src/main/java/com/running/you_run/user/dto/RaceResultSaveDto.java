// 저장용

package com.running.you_run.user.dto;

import lombok.*;

import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaceResultSaveDto {
    private String raceName;              // 대회 이름
    private LocalDate raceDate;       // 대회 일자
    private Double resultTime;            // 기록 (초, 혹은 hh:mm:ss string 확정 필요)
    private Double pace;                  // 평균 페이스 (min/km)
    private Integer rank;                 // 순위
    private double distance;
    private Double averagePace;
}
