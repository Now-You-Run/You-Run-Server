package com.running.you_run.running.util;

import com.running.you_run.running.payload.dto.CoordinateDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CoordinateConverter {
    public static LineString createLineString(List<CoordinateDto> path) {
        Coordinate[] coords = path.stream()
                .map(dto -> new Coordinate(dto.longitude(), dto.latitude()))
                .toArray(Coordinate[]::new);
        return new GeometryFactory().createLineString(coords);
    }
    public static List<CoordinateDto> convertLineStringToCoordinates(LineString lineString){
        Coordinate[] coordinates = lineString.getCoordinates();

        List<CoordinateDto> pathDto = Arrays.stream(coordinates)
                .map(coord -> new CoordinateDto(coord.y, coord.x)) // JTS: y가 위도, x가 경도
                .collect(Collectors.toList());
        return pathDto;
    }
}
