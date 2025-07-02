package com.running.you_run.running.entity;

import com.running.you_run.running.Enum.RunningMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

    @Column(nullable = true)
    private Long trackId;

    @Column(nullable = true)
    private Long opponentId;
    @Column(nullable = true)
    private boolean isWinner;
    private int duration;

    public Record() {}
}
