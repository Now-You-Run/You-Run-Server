package com.running.you_run.running.payload.dto;

public record TrackRecordDto(
        Long recordId,
        Long userId,
        String username,
        String grade,
        int level,
        long duration
) {
}
