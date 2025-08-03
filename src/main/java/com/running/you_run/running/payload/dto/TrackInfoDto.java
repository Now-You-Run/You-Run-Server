package com.running.you_run.running.payload.dto;

import com.running.you_run.running.entity.RunningTrack;
import com.running.you_run.running.util.CoordinateConverter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;

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

    public static TrackInfoDto convertToSimplifiedResponseDto(RunningTrack track) {
        double epsilon = 0.0005;
        LineString simplifiedLine = (LineString) DouglasPeuckerSimplifier.simplify(track.getPath(), epsilon);
        List<CoordinateDto> path = CoordinateConverter.convertLineStringToCoordinates(simplifiedLine);

        return new TrackInfoDto(
                path,
                track.getTotalDistance(),
                track.getName(),
                track.getRate()
        );
    }
}
