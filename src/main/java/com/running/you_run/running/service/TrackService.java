package com.running.you_run.running.service;

import com.running.you_run.global.exception.ApiException;
import com.running.you_run.global.exception.ErrorCode;
import com.running.you_run.running.Enum.TrackStatus;
import com.running.you_run.running.entity.Record;
import com.running.you_run.running.entity.RunningTrack;
import com.running.you_run.running.payload.dto.*;
import com.running.you_run.running.payload.request.RunningTrackStoreRequest;
import com.running.you_run.running.payload.response.MyTrackRecordListResponse;
import com.running.you_run.running.payload.response.TrackListResponse;
import com.running.you_run.running.payload.response.TrackPagesResponse;
import com.running.you_run.running.payload.response.TrackRecordResponse;
import com.running.you_run.running.repository.RecordRepository;
import com.running.you_run.running.repository.TrackRepository;
import com.running.you_run.user.entity.User;
import com.running.you_run.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackService {
    private final TrackRepository trackRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final KakaoGeoService kakaoGeoService;
    private final StaticMapService staticMapService;
    private final S3UploadService s3UploadService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Transactional
    public Long storeTrack(RunningTrackStoreRequest request) {
        // 입력 검증
        if (request.path() == null || request.path().isEmpty()) {
            throw new ApiException(ErrorCode.INVALID_TRACK_PATH);
        }

        if (request.path().size() < 2) {
            throw new ApiException(ErrorCode.INSUFFICIENT_TRACK_POINTS);
        }

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
                .trackStatus(TrackStatus.AVAILABLE)
                .build();

        RunningTrack savedTrack = trackRepository.save(track);

        // 비동기로 썸네일 생성
        generateAndUploadThumbnailAsync(savedTrack.getId(), request.path());

        return savedTrack.getId();
    }
    @Transactional
    public void deleteMyTrack(Long trackId, Long userId){
        RunningTrack runningTrack = trackRepository.findById(trackId)
                .orElseThrow(() -> new ApiException(ErrorCode.TRACK_NOT_EXIST));
        if (!runningTrack.getUser().getId().equals(userId)) {
            throw new ApiException(ErrorCode.USER_UNAUTHORIZED); // 또는 권한 없음 에러 코드
        }
        runningTrack.setTrackStatus(TrackStatus.DELETED);
    }

    @Transactional
    public Long storeServerTrack(RunningTrackStoreRequest request) {
        // 입력 검증
        if (request.path() == null || request.path().isEmpty()) {
            throw new ApiException(ErrorCode.INVALID_TRACK_PATH);
        }

        if (request.path().size() < 2) {
            throw new ApiException(ErrorCode.INSUFFICIENT_TRACK_POINTS);
        }


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
                .trackStatus(TrackStatus.AVAILABLE)
                .build();

        RunningTrack savedTrack = trackRepository.save(track);

        // 비동기로 썸네일 생성
        generateAndUploadThumbnailAsync(savedTrack.getId(), request.path());

        return savedTrack.getId();
    }

    @Transactional
    public TrackInfoDto getTrack(Long trackId) {
        RunningTrack track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ApiException(ErrorCode.TRACK_NOT_EXIST));
        return TrackInfoDto.convertToResponseDto(track);
    }

    @Transactional
    public TrackRecordResponse getServerTrackRecordResponse(Long trackId) {
        RunningTrack track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ApiException(ErrorCode.TRACK_NOT_EXIST));

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
                            entity.getId(),
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
    public MyTrackRecordListResponse getMyTrackRecordResponse(Long trackId) {
        RunningTrack track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ApiException(ErrorCode.TRACK_NOT_EXIST));

        List<Record> recordEntities = recordRepository
                .findByTrackIdOrderByResultTime(trackId);

        List<MyTrackRecordListItemDto> records = recordEntities.stream()
                .map(entity -> {
                    return new MyTrackRecordListItemDto(
                            entity.getId(),
                            entity.getResultTime(),
                            entity.getFinishedAt()
                    );
                })
                .collect(Collectors.toList());

        TrackInfoDto trackInfoDto = TrackInfoDto.convertToResponseDto(track);

        return new MyTrackRecordListResponse(trackInfoDto, records);
    }

    @Transactional
    public TrackListResponse getAllTrackRecords() {
        List<RunningTrack> allTracks = trackRepository.findAll();

        List<TrackListItemDto> trackListItemDtos = TrackListResponse.convertRunningTracksToTrackListItemDto(allTracks);
        trackListItemDtos.sort(Comparator.comparingInt(TrackListItemDto::distance));
        return new TrackListResponse(trackListItemDtos);
    }

    @Transactional
    public TrackPagesResponse getTracksOrderByClose(int page, int size, double userLon, double userLat) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RunningTrack> tracksPage = trackRepository.findTracksOrderByClose(
                userLon, userLat, pageable
        );
        List<TrackListItemDto> trackListItemDtos = TrackListResponse.convertRunningTracksToTrackListItemDto(tracksPage.getContent());
        int totalPages = tracksPage.getTotalPages();
        long totalElements = tracksPage.getTotalElements();
        return new TrackPagesResponse(
                trackListItemDtos,
                totalPages,
                totalElements
        );
    }

    @Transactional
    public TrackPagesResponse getUserTracksOrderByClose(int page, int size, long userId, double userLon, double userLat) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        Pageable pageable = PageRequest.of(page, size);
        Page<RunningTrack> tracksPage = trackRepository.findUserTracksOrderByClose(
                userLon, userLat, userId, pageable
        );
        return TrackListResponse
                .convertRunningTracksToTrackPagesResponse(
                        tracksPage.getContent(),
                        tracksPage.getTotalPages(),
                        tracksPage.getTotalElements()
                );
    }

//    @Transactional
//    public TrackPagesResponse getTracksOrderByTotalDistance(int page, int size, String order) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<RunningTrack> tracksPage;
//        if (order.equals("asc")){
//            tracksPage = trackRepository.findAllPublicAvailableTracksOrderByTotalDistanceAsc(pageable);
//        } else {
//            tracksPage = trackRepository.findAllPublicAvailableTracksOrderByTotalDistanceDesc(pageable);
//        }
//        return TrackListResponse
//                .convertRunningTracksToTrackPagesResponse(
//                        tracksPage.getContent(),
//                        tracksPage.getTotalPages(),
//                        tracksPage.getTotalElements()
//                );
//    }
//
//    @Transactional
//    public TrackPagesResponse getUserTracksOrderByTotalDistance(int page, int size, String order, long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
//        Pageable pageable = PageRequest.of(page, size);
//        Page<RunningTrack> tracksPage;
//        if (order.equals("asc")){
//            tracksPage = trackRepository.findUserAvailableTracksOrderByTotalDistanceAsc(userId,pageable);
//        } else {
//            tracksPage = trackRepository.findUserAvailableTracksOrderByTotalDistanceDesc(userId,pageable);
//        }
//
//        return TrackListResponse
//                .convertRunningTracksToTrackPagesResponse(
//                        tracksPage.getContent(),
//                        tracksPage.getTotalPages(),
//                        tracksPage.getTotalElements()
//                );
//    }

    @Transactional
    public TrackPagesResponse getUserTracksOrderByTotalDistance(int page, int size, String order, Long userId) {
        // 1. Sort 객체 생성
        Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "totalDistance"); // 정렬 기준 설정

        // 2. Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size, sort); // PageRequest에 Sort 객체 포함

        Page<RunningTrack> tracksPage;

        // 3. userId 유무에 따라 다른 Repository 메소드 호출
        if (userId != null) {
            tracksPage = trackRepository.findUserAvailableTracks(userId, pageable);
        } else {
            tracksPage = trackRepository.findAllPublicAvailableTracks(pageable);
        }

        return TrackListResponse
                .convertRunningTracksToTrackPagesResponse(
                        tracksPage.getContent(),
                        tracksPage.getTotalPages(),
                        tracksPage.getTotalElements()
                );
    }


    @Async
    public void generateAndUploadThumbnailAsync(Long trackId, List<CoordinateDto> trackPoints) {
        try {
            byte[] thumbnailData = staticMapService.generateTrackThumbnail(
                    trackPoints, 300, 200
            );

            String fileName = "track_" + trackId + "_" + System.currentTimeMillis();
            String thumbnailUrl = s3UploadService.uploadThumbnail(thumbnailData, fileName);

            updateThumbnailUrl(trackId, thumbnailUrl);
            log.info("썸네일 생성 완료 - Track ID: {}, URL: {}", trackId, thumbnailUrl);

        } catch (Exception e) {
            log.error("썸네일 생성 실패 - Track ID: {}", trackId, e);
            handleThumbnailGenerationFailure(trackId); // 실패 시에만 호출
        }
    }


    @Transactional
    public void updateThumbnailUrl(Long trackId, String thumbnailUrl) {
        RunningTrack track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ApiException(ErrorCode.TRACK_NOT_EXIST));
        track.setThumbnailUrl(thumbnailUrl);
        trackRepository.save(track);
    }

    private void handleThumbnailGenerationFailure(Long trackId) {
        try {
            String defaultThumbnailUrl = "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/default-track-thumbnail.jpg";
            updateThumbnailUrl(trackId, defaultThumbnailUrl);
            log.warn("기본 썸네일로 설정 - Track ID: {}", trackId);
        } catch (Exception e) {
            log.error("기본 썸네일 설정도 실패 - Track ID: {}", trackId, e);
        }
    }
}
