package com.running.you_run.running.payload.dto;

import com.running.you_run.running.entity.RunningTrack;
import org.locationtech.jts.geom.Coordinate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record TrackInfoDto(
        List<CoordinateResponseDto> path,
        int totalDistance,
        String name,
        double rate
) {
    public static TrackInfoDto convertToResponseDto(RunningTrack track) {
        // LineString에서 좌표 배열을 가져옴
        Coordinate[] coordinates = track.getPath().getCoordinates();

        // 좌표 배열을 List<CoordinateResponseDto>로 변환
        List<CoordinateResponseDto> pathDto = Arrays.stream(coordinates)
                .map(coord -> new CoordinateResponseDto(coord.y, coord.x)) // JTS: y가 위도, x가 경도
                .collect(Collectors.toList());

        return new TrackInfoDto(
                pathDto,
                track.getTotalDistance(),
                track.getName(),
                track.getRate()
        );
    }
}
