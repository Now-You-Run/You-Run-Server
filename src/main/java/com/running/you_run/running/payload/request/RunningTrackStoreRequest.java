package com.running.you_run.running.payload.request;

import com.running.you_run.running.payload.dto.CoordinateDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import java.util.List;

public record RunningTrackStoreRequest(
        String name,
        int totalDistance,
        double rate,
        List<CoordinateDto> path,
        int userId
) {
    public LineString createLineString() {
        Coordinate[] coords = path.stream()
                .map(dto -> new Coordinate(dto.longitude(), dto.latitude()))
                .toArray(Coordinate[]::new);
        return new GeometryFactory().createLineString(coords);
    }
}

