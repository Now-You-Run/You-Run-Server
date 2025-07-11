package com.running.you_run.running.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.running.you_run.running.Enum.RunningMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.LineString;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@AllArgsConstructor
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long userId;
    @Column
    @Enumerated(EnumType.STRING)
    private RunningMode mode;
    @Column
    private Long trackId;
    @Column(nullable = true)
    private Long opponentId;
    @Column
    private boolean isWinner;
    @Column(nullable = false)
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private double resultTime;
    private double distance;
    private double averagePace;
    @Column(nullable = false)
    private boolean isPersonalBest = false; // 기본값은 false
    @JsonIgnore
    @Column(name = "user_path", columnDefinition = "geometry(LineString,4326)", nullable = true)
    private LineString path;
    @Column
    private Integer botPace;


    public Record() {}
    public void updateRecord(LocalDateTime finishedAt, double resultTime, double distance, double averagePace, boolean isWinner, Long opponentId) {
        this.finishedAt = finishedAt;
        this.resultTime = resultTime;
        this.distance = distance;
        this.averagePace = averagePace;
        this.isWinner = isWinner;
        this.opponentId = opponentId;
    }
    public void markAsPersonalBest() {
        this.isPersonalBest = true;
    }

    public void unmarkAsPersonalBest() {
        this.isPersonalBest = false;
    }
}