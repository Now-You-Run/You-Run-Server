package com.running.you_run.running.payload.dto;

public record TrackRecordDto(
        Long userId,
        String username,
        int duration
) {
}
