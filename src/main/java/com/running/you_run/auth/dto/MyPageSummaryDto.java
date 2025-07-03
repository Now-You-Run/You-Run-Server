package com.running.you_run.auth.dto;

import com.running.you_run.auth.dto.RaceResultDto;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPageSummaryDto {
    private String name;
    private int level;
    private int experience;
    private double weeklyDistance;
    private double averagePace;
    private int runningCount;
    private List<RaceResultDto> recentRaceResults;
}
