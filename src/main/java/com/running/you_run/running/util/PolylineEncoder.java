package com.running.you_run.running.util;

import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.LatLng;
import com.running.you_run.running.payload.dto.CoordinateDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PolylineEncoder {
    /**
     * TrackPoint 리스트를 Google Polyline으로 인코딩
     */
    public String encodeTrackPoints(List<CoordinateDto> trackPoints) {
        List<LatLng> latLngs = trackPoints.stream()
                .map(point -> new LatLng(point.latitude(), point.longitude()))
                .collect(Collectors.toList());

        return PolylineEncoding.encode(latLngs);
    }

    /**
     * 위도/경도 리스트를 Polyline으로 인코딩
     */
    public String encodeCoordinates(List<Double> latitudes, List<Double> longitudes) {
        if (latitudes.size() != longitudes.size()) {
            throw new IllegalArgumentException("위도와 경도 리스트의 크기가 다릅니다.");
        }

        List<LatLng> latLngs = new ArrayList<>();
        for (int i = 0; i < latitudes.size(); i++) {
            latLngs.add(new LatLng(latitudes.get(i), longitudes.get(i)));
        }

        return PolylineEncoding.encode(latLngs);
    }

    /**
     * Polyline 문자열을 LatLng 리스트로 디코딩
     */
    public List<LatLng> decodePolyline(String encodedPolyline) {
        return PolylineEncoding.decode(encodedPolyline);
    }
}
