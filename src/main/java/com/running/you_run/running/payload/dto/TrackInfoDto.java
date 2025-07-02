package com.running.you_run.running.payload.dto;

import com.running.you_run.running.entity.RunningTrack;
import com.running.you_run.running.util.CoordinateConverter;
import org.locationtech.jts.geom.Coordinate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record TrackInfoDto(
        List<CoordinateDto> path,
        int totalDistance,
        String name,
        double rate
) {
    public static TrackInfoDto convertToResponseDto(RunningTrack track) {
        List<CoordinateDto> path = CoordinateConverter.convertLineStringToCoordinates(track.getPath());

        return new TrackInfoDto(
                path,
                track.getTotalDistance(),
                track.getName(),
                track.getRate()
        );
    }
}
