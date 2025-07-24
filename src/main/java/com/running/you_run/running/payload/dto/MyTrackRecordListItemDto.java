package com.running.you_run.running.payload.dto;

import java.time.LocalDateTime;

public record MyTrackRecordListItemDto(
        Long recordId,
        double resultTime,
        LocalDateTime finishedAt
) {
}
