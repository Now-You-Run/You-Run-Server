package com.running.you_run.running.service;

import com.running.you_run.global.exception.ApiException;
import com.running.you_run.global.exception.ErrorCode;
import com.running.you_run.running.entity.RunningTrack;
import com.running.you_run.running.payload.dto.TrackInfoDto;
import com.running.you_run.running.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackInfoService {
    private final TrackRepository trackRepository;

    @Cacheable(value = "track-info", key = "#trackId")
    public TrackInfoDto getCacheTrackInfo(Long trackId) {
        RunningTrack track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ApiException(ErrorCode.TRACK_NOT_EXIST));
        return TrackInfoDto.convertToResponseDto(track);
    }

    public TrackInfoDto getTrackInfo(Long trackId){
        RunningTrack track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ApiException(ErrorCode.TRACK_NOT_EXIST));
        return TrackInfoDto.convertToResponseDto(track);
    }
}
