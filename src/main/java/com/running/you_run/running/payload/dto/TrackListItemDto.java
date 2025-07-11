package com.running.you_run.running.payload.dto;

import com.running.you_run.running.entity.RunningTrack;
import com.running.you_run.running.util.CoordinateConverter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;

import java.util.List;

public record TrackListItemDto(
        Long id,
        String name,
        int distance,
        List<CoordinateDto> path

) {
    public static TrackListItemDto from(RunningTrack track){
        double epsilon = 0.0002;
        LineString simplifiedLine = (LineString) DouglasPeuckerSimplifier.simplify(track.getPath(), epsilon);
        List<CoordinateDto> coordinateDtos = CoordinateConverter.convertLineStringToCoordinates(simplifiedLine);
        return new TrackListItemDto(track.getId(), track.getName(), track.getTotalDistance(),coordinateDtos);
    }

}
