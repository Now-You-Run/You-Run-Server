package com.running.you_run.running.service;

import com.running.you_run.global.exception.ApiException;
import com.running.you_run.global.exception.ErrorCode;
import com.running.you_run.running.entity.Record;
import com.running.you_run.running.entity.RunningTrack;
import com.running.you_run.running.payload.dto.RecordDto;
import com.running.you_run.running.payload.dto.TrackInfoDto;
import com.running.you_run.running.payload.request.RecordStoreRequest;
import com.running.you_run.running.repository.RecordRepository;
import com.running.you_run.running.repository.TrackRepository;
import com.running.you_run.user.entity.User;
import com.running.you_run.user.repository.UserRepository;
import com.running.you_run.user.util.LevelCalculator;
import com.running.you_run.user.util.PointCalculator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordService {
    private final RecordRepository recordRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final LevelCalculator levelCalculator;
    private final PointCalculator pointCalculator;
    @Transactional
    public Record storeRecord(RecordStoreRequest request) {
        User user = updateUserStats(request.userId(), request.distance());

        double newResultTime = request.calculateResultTime();
        Record newRecord = request.toRecord(newResultTime);

        updatePersonalBest(newRecord, request.userId(), request.trackId());

        return recordRepository.save(newRecord);
    }
    @Transactional
    public List<RecordDto> findAllRecordById(Long userId) {
        List<Record> records = recordRepository.findByUserId(userId);
        if (records.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> trackIds = records.stream()
                .map(Record::getTrackId)
                .distinct()
                .toList();

        // (Key: trackId, Value: RunningTrack)
        Map<Long, RunningTrack> trackMap = trackRepository.findAllById(trackIds).stream()
                .collect(Collectors.toMap(RunningTrack::getId, track -> track));

        List<RecordDto> recordDtos = new ArrayList<>();
        for (Record record : records) {
            RunningTrack track = trackMap.get(record.getTrackId());
            if (track != null) {
                recordDtos.add(RecordDto.from(record, TrackInfoDto.convertToResponseDto(track)));
            }
        }
        return recordDtos;
    }

    private User updateUserStats(Long userId, long distance) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));

        int newLevel = levelCalculator.calculateLevelByTotalDistance(user.getTotalDistance(), distance);
        long gainedPoints = pointCalculator.calculatePoint(distance);
        user.applyRunningResult(distance, gainedPoints, newLevel);

        // @Transactional 환경이므로, 변경된 user는 메소드 종료 시 자동으로 DB에 반영됩니다.
        // 따라서 userRepository.save(user)를 명시적으로 호출할 필요가 없습니다. (더티 체킹)
        return user;
    }

    private void updatePersonalBest(Record newRecord, Long userId, Long trackId) {
        recordRepository.findByUserIdAndTrackIdAndIsPersonalBestIsTrue(userId, trackId)
                .ifPresentOrElse(
                        // 기존 최고 기록이 있을 경우
                        oldBestRecord -> {
                            if (newRecord.getResultTime() < oldBestRecord.getResultTime()) {
                                oldBestRecord.unmarkAsPersonalBest();
                                newRecord.markAsPersonalBest();
                            }
                        },
                        // 기존 최고 기록이 없을 경우 (첫 기록)
                        () -> newRecord.markAsPersonalBest()
                );
    }
}
