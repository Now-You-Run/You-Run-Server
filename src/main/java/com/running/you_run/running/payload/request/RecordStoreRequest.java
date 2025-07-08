package com.running.you_run.running.payload.request;

import com.running.you_run.running.Enum.RunningMode;
import com.running.you_run.running.entity.Record;

import java.time.LocalDateTime;

public record RecordStoreRequest(
        Long userId,
        String mode,
        Long trackId,
        Long opponentId,
        Boolean isWinner,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        double averagePace,
        double distance
) {
    public Record toRecord(){
        return Record.builder()
                .userId(userId())
                .mode(mode() == null ? null : RunningMode.valueOf(mode()))
                .trackId(trackId())
                .opponentId(opponentId())
                .isWinner(isWinner())
                .startedAt(startedAt)
                .finishedAt(finishedAt)
                .averagePace(averagePace)
                .distance(distance)
                .build();
    }
}
