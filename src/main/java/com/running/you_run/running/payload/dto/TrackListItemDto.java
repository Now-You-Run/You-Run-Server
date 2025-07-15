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
        String thumbnailUrl

) {
    public static TrackListItemDto from(RunningTrack track){
        return new TrackListItemDto(track.getId(), track.getName(), track.getTotalDistance(), track.getThumbnailUrl());
    }

}
