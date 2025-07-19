package com.running.you_run.running.service;

import com.running.you_run.running.payload.dto.CoordinateDto;
import com.running.you_run.running.util.PolylineEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class StaticMapService {
    @Value("${static-maps.google.api-key}")
    private String googleApiKey;

    @Value("${static-maps.google.base-url}")
    private String baseUrl;

    private final PolylineEncoder polylineEncoder;

    public byte[] generateTrackThumbnail(List<CoordinateDto> trackPoints, int width, int height) {
        try {
            String encodedPath = encodePolyline(trackPoints);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("size", String.format("%dx%d", width, height))
                    .queryParam("path", "enc:" + encodedPath) // 'enc:' 접두사는 그대로 붙여서 파라미터로
                    .queryParam("key", googleApiKey)
                    .queryParam("format", "jpg");

            // encode()를 사용하여 안전하게 인코딩된 URI를 생성
            URI uri = builder.build().encode().toUri();
            log.info("만들어진 썸네일 url" + uri);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<byte[]> response = restTemplate.getForEntity(uri, byte[].class);

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
