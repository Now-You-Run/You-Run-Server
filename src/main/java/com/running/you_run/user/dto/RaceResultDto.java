// 조회용

package com.running.you_run.user.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaceResultDto {
    private Long id;
    private String raceName;
    private LocalDate raceDate;
    private Double resultTime;
    private Double pace;;
    private Double distance;
    private Double averagePace;
}
