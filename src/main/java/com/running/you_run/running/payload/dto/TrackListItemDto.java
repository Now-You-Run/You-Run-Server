package com.running.you_run.running.payload.dto;

import com.running.you_run.running.entity.RunningTrack;
import com.running.you_run.running.util.CoordinateConverter;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;

public record TrackListItemDto(
        Long id,
        String name,
        int distance,
        List<CoordinateDto> path

) {
    public static TrackListItemDto from(RunningTrack track){
        List<CoordinateDto> coordinateDtos = CoordinateConverter.convertLineStringToCoordinates(track.getPath());
        return new TrackListItemDto(track.getId(), track.getName(), track.getTotalDistance(),coordinateDtos);
    }

}
