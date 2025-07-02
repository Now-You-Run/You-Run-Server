package com.running.you_run.running.payload.response;

import com.running.you_run.running.entity.RunningTrack;
import com.running.you_run.running.payload.dto.TrackListItemDto;
import com.running.you_run.running.util.CoordinateConverter;

import java.util.List;
import java.util.stream.Collectors;

public record TrackListResponse(
    List<TrackListItemDto> tracks
){
    public static List<TrackListItemDto> convertRunningTracksToTrackListResponse(List<RunningTrack> tracks) {
        return tracks.stream()
                .map(TrackListItemDto::from) // static 메서드 사용
                .collect(Collectors.toList());
    }
}
