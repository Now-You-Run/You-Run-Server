package com.running.you_run.running.payload.request;

import com.running.you_run.running.Enum.RunningMode;
import com.running.you_run.running.entity.Record;
import com.running.you_run.running.payload.dto.CoordinateDto;
import com.running.you_run.running.util.CoordinateConverter;
import org.locationtech.jts.geom.LineString;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

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
        List<CoordinateDto> userPath
) {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Record toRecord(double resultTime){
        LineString path = CoordinateConverter.createLineString(userPath);

        String rawJson;
        try {
            rawJson = MAPPER.writeValueAsString(userPath);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("userPath JSON 직렬화 실패", e);
        }

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
                .rawPathJson(rawJson)
                .build();
    }

    public double calculateResultTime() {
        if (startedAt == null || finishedAt == null) {
            return 0.0;
        }
        return Duration.between(startedAt, finishedAt).toMillis() / 1000.0;
    }
}
