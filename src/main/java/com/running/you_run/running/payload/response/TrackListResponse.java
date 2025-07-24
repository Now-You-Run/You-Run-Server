package com.running.you_run.running.payload.response;

import com.running.you_run.running.entity.RunningTrack;
import com.running.you_run.running.payload.dto.TrackListItemDto;

import java.util.List;
import java.util.stream.Collectors;

public record TrackListResponse(
        List<TrackListItemDto> tracks

) {

    public static List<TrackListItemDto> convertRunningTracksToTrackListItemDto(
            List<RunningTrack> tracks
    ) {
        return tracks.stream()
                .map(TrackListItemDto::from) // static 메서드 사용
                .collect(Collectors.toList());
    }
    public static TrackPagesResponse convertRunningTracksToTrackPagesResponse(
            List<RunningTrack> tracks,
            int totalPages,
            long totalElements
    ) {
        List<TrackListItemDto> collect = tracks.stream()
                .map(TrackListItemDto::from) // static 메서드 사용
                .collect(Collectors.toList());
        return new TrackPagesResponse(
                collect,
                totalPages,
                totalElements
        );
    }
}
