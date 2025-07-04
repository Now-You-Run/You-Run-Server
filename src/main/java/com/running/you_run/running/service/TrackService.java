package com.running.you_run.running.service;

import com.running.you_run.auth.entity.User;
import com.running.you_run.auth.repository.UserRepository;
import com.running.you_run.global.exception.ApiException;
import com.running.you_run.global.exception.ErrorCode;
import com.running.you_run.running.entity.Record;
import com.running.you_run.running.entity.RunningTrack;
import com.running.you_run.running.payload.dto.TrackListItemDto;
import com.running.you_run.running.payload.dto.TrackRecordDto;
import com.running.you_run.running.payload.request.RunningTrackStoreRequest;
import com.running.you_run.running.payload.dto.TrackInfoDto;
import com.running.you_run.running.payload.response.TrackListResponse;
import com.running.you_run.running.payload.response.TrackRecordResponse;
import com.running.you_run.running.repository.RecordRepository;
import com.running.you_run.running.repository.TrackRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TrackService {
    private final TrackRepository trackRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final KakaoGeoService kakaoGeoService;
    @Transactional
    public Long storeTrack(RunningTrackStoreRequest request) {
        double startLatitude = request.path().get(0).latitude();
        double startLongitude = request.path().get(0).longitude();
        String address = kakaoGeoService.getCityDistrict(startLatitude,startLongitude);
        RunningTrack track = RunningTrack.builder()
                .name(request.name())
                .totalDistance(request.totalDistance())
                .rate(request.rate())
                .path(request.createLineString())
                .startLatitude(startLatitude)
                .startLongitude(startLongitude)
                .address(address)
                .build();

        trackRepository.save(track);
        return track.getId();
    }

    @Transactional
    public TrackInfoDto returnTrack(Long trackId){
        RunningTrack track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ApiException(ErrorCode.TOKEN_INVALID));
        return TrackInfoDto.convertToResponseDto(track);
    }
    @Transactional
    public TrackRecordResponse returnTrackRecordResponse(Long trackId) {
        RunningTrack track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ApiException(ErrorCode.TOKEN_INVALID));

        List<Record> recordEntities = recordRepository.findByTrackId(trackId);

        List<TrackRecordDto> records = recordEntities.stream()
                .map(entity -> {
                    // userId로 User 엔티티를 조회
                    User user = userRepository.findById(entity.getUserId())
                            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
                    return new TrackRecordDto(
                            entity.getUserId(),
                            user.getName(), // 또는 getNickname() 등
                            entity.getDuration()
                    );
                })
                .collect(Collectors.toList());

        TrackInfoDto trackInfoDto = TrackInfoDto.convertToResponseDto(track);

        return new TrackRecordResponse(trackInfoDto, records);
    }

    @Transactional
    public TrackListResponse returnAllTrackRecordResponses(){
        List<RunningTrack> allTracks = trackRepository.findAll();

        List<TrackListItemDto> trackListItemDtos = TrackListResponse.convertRunningTracksToTrackListResponse(allTracks);
        trackListItemDtos.sort(Comparator.comparingInt(TrackListItemDto::distance));
        return new TrackListResponse(trackListItemDtos);
    }
}
