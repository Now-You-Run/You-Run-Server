package com.running.you_run.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "race_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String raceName;
    private LocalDateTime raceDate;
    private Double resultTime; // 기록 (초)
    private Double pace;       // min/km
    private Integer rank;      // 순위
    private double distance;
    private double averagePace;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
