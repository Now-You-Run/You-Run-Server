package com.running.you_run.running.payload.response;

import com.running.you_run.running.payload.dto.TrackListItemDto;

import java.util.List;

public record TrackPagesResponse(
        List<TrackListItemDto> tracks,
        int totalPages,
        long totalElements
) {
}
