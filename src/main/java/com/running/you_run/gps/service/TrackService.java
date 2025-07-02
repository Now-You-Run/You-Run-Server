package com.running.you_run.gps.service;

import com.running.you_run.global.exception.ApiException;
import com.running.you_run.global.exception.ErrorCode;
import com.running.you_run.gps.entity.RunningTrack;
import com.running.you_run.gps.payload.request.TrackStoreRequest;
import com.running.you_run.gps.payload.response.TrackPathResponse;
import com.running.you_run.gps.repository.TrackRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TrackService {
    private final TrackRepository trackRepository;
    @Transactional
    public void storeTrack(TrackStoreRequest request) {
        // 1. DTO에서 LineString 생성
        LineString pathLineString = request.createLineString();

        // 2. DTO와 LineString을 사용하여 엔티티 생성
        RunningTrack newTrack = RunningTrack.builder()
                .userId(request.getUserId())
                .path(pathLineString)
                .timestamp(LocalDateTime.now().toString()) // 혹은 첫 좌표의 timestamp 사용
                .distance(request.getDistance())
                .build();

        // 3. 데이터베이스에 저장
        trackRepository.save(newTrack);
    }

    @Transactional
    public TrackPathResponse returnTrack(Long trackId){
        RunningTrack track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ApiException(ErrorCode.TOKEN_INVALID));
        return TrackPathResponse.convertToResponseDto(track);
    }
}
