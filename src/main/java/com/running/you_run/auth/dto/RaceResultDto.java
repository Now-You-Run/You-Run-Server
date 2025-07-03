package com.running.you_run.auth.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaceResultDto {
    private String raceName;
    private LocalDateTime raceDate;
    private Double resultTime;
    private Double pace;
    private Integer rank;
}
