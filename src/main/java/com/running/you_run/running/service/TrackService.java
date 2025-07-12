package com.running.you_run.running.service;

import com.running.you_run.global.exception.ApiException;
import com.running.you_run.global.exception.ErrorCode;
import com.running.you_run.running.entity.Record;
import com.running.you_run.running.entity.RunningTrack;
import com.running.you_run.running.payload.dto.TrackInfoDto;
import com.running.you_run.running.payload.dto.TrackListItemDto;
import com.running.you_run.running.payload.dto.TrackRecordDto;
import com.running.you_run.running.payload.request.RunningTrackStoreRequest;
import com.running.you_run.running.payload.response.TrackListResponse;
import com.running.you_run.running.payload.response.TrackPagesResponse;
import com.running.you_run.running.payload.response.TrackRecordResponse;
import com.running.you_run.running.repository.RecordRepository;
import com.running.you_run.running.repository.TrackRepository;
import com.running.you_run.user.entity.User;
import com.running.you_run.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        double startLatitude = request.path().get(0).latitude();
        double startLongitude = request.path().get(0).longitude();
        String address = kakaoGeoService.getCityDistrict(startLatitude, startLongitude);
        RunningTrack track = RunningTrack.builder()
                .name(request.name())
                .totalDistance(request.totalDistance())
                .rate(request.rate())
                .path(request.createLineString())
                .startLatitude(startLatitude)
                .startLongitude(startLongitude)
                .address(address)
                .user(user)
                .build();
        trackRepository.save(track);
        return track.getId();
    }

    @Transactional
    public TrackInfoDto returnTrack(Long trackId) {
        RunningTrack track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ApiException(ErrorCode.TOKEN_INVALID));
        return TrackInfoDto.convertToResponseDto(track);
    }

    @Transactional
    public TrackRecordResponse returnTrackRecordResponse(Long trackId) {
        RunningTrack track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ApiException(ErrorCode.TOKEN_INVALID));

        List<Record> recordEntities = recordRepository
                .findByTrackIdAndIsPersonalBestTrueOrderByResultTimeAsc(trackId);

        // 2. Collect all user IDs from the records
        List<Long> userIds = recordEntities.stream()
                .map(Record::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 3. Second query: Fetch all necessary users in a single batch
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<TrackRecordDto> records = recordEntities.stream()
                .map(entity -> {
                    User user = userMap.get(entity.getUserId());
                    return new TrackRecordDto(
                            entity.getUserId(),
                            user.getName(),
                            user.getGrade().getName(),
                            user.getLevel(),
                            (long) entity.getResultTime() // Use the pre-calculated time
                    );
                })
                .collect(Collectors.toList());

        TrackInfoDto trackInfoDto = TrackInfoDto.convertToResponseDto(track);

        return new TrackRecordResponse(trackInfoDto, records);
    }

    @Transactional
    public TrackListResponse returnAllTrackRecordResponses() {
        List<RunningTrack> allTracks = trackRepository.findAll();

        List<TrackListItemDto> trackListItemDtos = TrackListResponse.convertRunningTracksToTrackListResponse(allTracks);
        trackListItemDtos.sort(Comparator.comparingInt(TrackListItemDto::distance));
        return new TrackListResponse(trackListItemDtos);
    }

    @Transactional
    public TrackPagesResponse returnAllTrackRecordResponsesOrderByDb(int page, int size, double userLon, double userLat) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RunningTrack> tracksPage = trackRepository.findTracksOrderByDistance(
                userLon, userLat, pageable
        );
        List<TrackListItemDto> trackListItemDtos = TrackListResponse.convertRunningTracksToTrackListResponse(tracksPage.getContent());
        int totalPages = tracksPage.getTotalPages();
        long totalElements = tracksPage.getTotalElements();
        return new TrackPagesResponse(
                trackListItemDtos,
                totalPages,
                totalElements
        );
    }

    @Transactional
    public TrackPagesResponse returnAllUserTrackRecordResponsesOrderByDb(int page, int size, long userId, double userLon, double userLat) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        Pageable pageable = PageRequest.of(page, size);
        Page<RunningTrack> tracksPage = trackRepository.findUserTracksOrderByDistance(
                userLon, userLat, userId, pageable
        );
        List<TrackListItemDto> trackListItemDtos = TrackListResponse.convertRunningTracksToTrackListResponse(tracksPage.getContent());
        int totalPages = tracksPage.getTotalPages();
        long totalElements = tracksPage.getTotalElements();
        return new TrackPagesResponse(
                trackListItemDtos,
                totalPages,
                totalElements
        );
    }
}
