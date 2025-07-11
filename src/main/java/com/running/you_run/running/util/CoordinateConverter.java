package com.running.you_run.running.util;

import com.running.you_run.running.payload.dto.CoordinateDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.CoordinateXYM;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CoordinateConverter {
    public static LineString createLineStringM(List<CoordinateDto> path) {
        CoordinateXYM[] coords = path.stream()
                .map(dto -> new CoordinateXYM(dto.longitude(), dto.latitude(),dto.timestamp()))
                .toArray(CoordinateXYM[]::new);
        return new GeometryFactory().createLineString(coords);
    }
    public static List<CoordinateDto> convertLineStringToCoordinates(LineString lineString){
        Coordinate[] coordinates = lineString.getCoordinates();
        return Arrays.stream(coordinates)
                .map(coord -> new CoordinateDto(coord.y, coord.x, coord.getM()))
                .collect(Collectors.toList());
    }
}
