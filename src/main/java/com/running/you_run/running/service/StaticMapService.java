package com.running.you_run.running.service;

import com.running.you_run.running.payload.dto.CoordinateDto;
import com.running.you_run.running.util.PolylineEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaticMapService {
    @Value("${static-maps.google.api-key}")
    private String googleApiKey;

    @Value("${static-maps.google.base-url}")
    private String baseUrl;

    private final PolylineEncoder polylineEncoder;

    public byte[] generateTrackThumbnail(List<CoordinateDto> trackPoints, int width, int height) {
        try {
            String encodedPath = encodePolyline(trackPoints);
            String url = String.format(
                    "%s?size=%dx%d&path=enc:%s&key=%s&format=jpg",
                    baseUrl, width, height, encodedPath, googleApiKey
            );

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Static map 생성 실패", e);
        }
    }

    public byte[] generateStyledThumbnail(List<CoordinateDto> trackPoints, int width, int height) {
        String encodedPath = polylineEncoder.encodeTrackPoints(trackPoints);

        String url = String.format(
                "%s?size=%dx%d&path=color:0x0000ff|weight:3|enc:%s&key=%s&format=jpg&maptype=roadmap",
                baseUrl, width, height, encodedPath, googleApiKey
        );

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        return response.getBody();
    }

    private String encodePolyline(List<CoordinateDto> points) {
        return polylineEncoder.encodeTrackPoints(points);
    }
}
