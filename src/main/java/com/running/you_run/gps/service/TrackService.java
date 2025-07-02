package com.running.you_run.gps.service;

import com.running.you_run.global.exception.ApiException;
import com.running.you_run.global.exception.ErrorCode;
import com.running.you_run.gps.entity.RunningTrack;
import com.running.you_run.gps.payload.request.RunningTrackStoreRequest;
import com.running.you_run.gps.payload.response.TrackPathResponse;
import com.running.you_run.gps.repository.TrackRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TrackService {
    private final TrackRepository trackRepository;
    @Transactional
    public Long storeTrack(RunningTrackStoreRequest request) {
        RunningTrack track = RunningTrack.builder()
                .name(request.name())
                .totalDistance(request.totalDistance())
                .rate(request.rate())
                .path(request.createLineString())
                .build();

        trackRepository.save(track);
        return track.getId();
    }

    @Transactional
    public TrackPathResponse returnTrack(Long trackId){
        RunningTrack track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ApiException(ErrorCode.TOKEN_INVALID));
        return TrackPathResponse.convertToResponseDto(track);
    }
}
