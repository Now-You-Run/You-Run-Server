package com.running.you_run.running.payload.request;

import com.running.you_run.running.Enum.RunningMode;
import com.running.you_run.running.entity.Record;
import com.running.you_run.running.payload.dto.CoordinateDto;
import com.running.you_run.running.util.CoordinateConverter;
import org.locationtech.jts.geom.LineString;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record RecordStoreRequest(
        Long userId,
        String mode,
        Long trackId,
        Long opponentId,
        Boolean isWinner,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        double averagePace,
        long distance,
        List<CoordinateDto> userPath,
        Integer botPace
) {
    public Record toRecord(double resultTime){
        LineString path = CoordinateConverter.createLineStringM(userPath);

        return Record.builder()
                .userId(userId())
                .mode(mode() == null ? null : RunningMode.valueOf(mode()))
                .trackId(trackId())
                .opponentId(opponentId())
                .isWinner(isWinner() != null && isWinner())
                .startedAt(startedAt)
                .finishedAt(finishedAt)
                .averagePace(averagePace)
                .distance(distance)
                .resultTime(resultTime)
                .isPersonalBest(false)
                .path(path)
                .botPace(botPace())
                .build();
    }

    public double calculateResultTime() {
        if (startedAt == null || finishedAt == null) {
            return 0.0;
        }
        return Duration.between(startedAt, finishedAt).toMillis() / 1000.0;
    }
}
