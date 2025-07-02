package com.running.you_run.gps.payload.request;

import com.running.you_run.gps.payload.dto.CoordinateDto;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
public class TrackStoreRequest {
    private Long userId;
    private List<CoordinateDto> path;
    private LocalDateTime date;
    private int distance; //미터 단위
    // 기본 생성자
    public TrackStoreRequest() {}

    // getter/setter
    public LineString createLineString() {
        Coordinate[] coords = path.stream()
                .map(dto -> new Coordinate(dto.longitude(), dto.latitude()))
                .toArray(Coordinate[]::new);
        return new GeometryFactory().createLineString(coords);
    }
}

