package com.running.you_run.running.service;

import com.running.you_run.global.exception.ApiException;
import com.running.you_run.running.entity.Record;
import com.running.you_run.running.fixture.RecordFixture;
import com.running.you_run.running.payload.request.RecordStoreRequest;
import com.running.you_run.running.repository.RecordRepository;
import com.running.you_run.user.entity.User;
import com.running.you_run.user.repository.UserRepository;
import com.running.you_run.user.util.LevelCalculator;
import com.running.you_run.user.util.PointCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {
    @InjectMocks
    private RecordService recordService; // The service we are testing
    @Mock
    private RecordRepository recordRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LevelCalculator levelCalculator;
    @Mock
    private PointCalculator pointCalculator;

    @Test
    @DisplayName("첫 기록 저장 시, 개인 최고 기록으로 설정되고 유저 정보가 업데이트된다")
    void storeRecord_firstTime_setsPersonalBest() {
        // Given (Arrange)
        long userId = 1L;
        long trackId = 101L;
        RecordStoreRequest request = RecordFixture.createRecordRequest(userId, trackId, 5000, 1800); // 5km, 30min

        User mockUser = spy(User.builder()
                .id(userId)
                .totalDistance(10000L)
                .level(1)
                .point(0L)
                .build());

        // Define mock behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(levelCalculator.calculateLevelByTotalDistance(anyDouble(), anyDouble())).thenReturn(5); // Assume new level is 5
        when(pointCalculator.calculatePoint(anyDouble())).thenReturn(100L); // Assume 100 points gained
        when(recordRepository.findByUserIdAndTrackIdAndIsPersonalBestIsTrue(userId, trackId)).thenReturn(Optional.empty());
        when(recordRepository.save(any(Record.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When (Act)
        Record savedRecord = recordService.storeRecord(request);

        // Then (Assert)
        // 1. Verify that user stats were updated
        verify(mockUser).applyRunningResult(eq(5000L), eq(100L), eq(5));

        // 2. Verify personal best logic
        assertTrue(savedRecord.isPersonalBest());

        // 3. Verify the save method was called
        verify(recordRepository, times(1)).save(any(Record.class));
    }

    @Test
    @DisplayName("새로운 기록이 기존 최고 기록보다 빠를 때, 최고 기록을 갱신한다")
    void storeRecord_newPersonalBest_updatesFlag() {
        // Given
        long userId = 1L;
        long trackId = 101L;
        RecordStoreRequest request = RecordFixture.createRecordRequest(userId, trackId, 5000, 1800); // New time: 30 min
        User mockUser = spy(User.builder()
                .id(userId)
                .totalDistance(10000L)
                .level(1)
                .point(0L)
                .build());

        Record oldBestRecord = spy(Record.builder().resultTime(2000.0).build()); // Old time: ~33 min

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(recordRepository.findByUserIdAndTrackIdAndIsPersonalBestIsTrue(userId, trackId)).thenReturn(Optional.of(oldBestRecord));
        when(recordRepository.save(any(Record.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Record newRecord = recordService.storeRecord(request);

        // Then
        verify(oldBestRecord, times(1)).unmarkAsPersonalBest();
        assertTrue(newRecord.isPersonalBest());
    }

    @Test
    @DisplayName("새로운 기록이 기존 최고 기록보다 느릴 때, 최고 기록을 갱신하지 않는다")
    void storeRecord_slowerThanPersonalBest_doesNotUpdateFlag() {
        // Given
        long userId = 1L;
        long trackId = 101L;
        RecordStoreRequest request = RecordFixture.createRecordRequest(userId, trackId, 5000, 2400); // New time: 40 min

        User mockUser = spy(User.builder()
                .id(userId)
                .totalDistance(10000L)
                .level(1)
                .point(0L)
                .build());

        Record oldBestRecord = spy(Record.builder().resultTime(1800.0).build()); // Old time: 30 min

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(recordRepository.findByUserIdAndTrackIdAndIsPersonalBestIsTrue(userId, trackId)).thenReturn(Optional.of(oldBestRecord));
        when(recordRepository.save(any(Record.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Record newRecord = recordService.storeRecord(request);

        // Then
        verify(oldBestRecord, never()).unmarkAsPersonalBest();
        assertFalse(newRecord.isPersonalBest());
    }

    @Test
    @DisplayName("존재하지 않는 유저 ID로 기록 저장 시, ApiException을 던진다")
    void storeRecord_userNotFound_throwsException() {
        // Given
        long nonExistentUserId = 999L;
        RecordStoreRequest request = RecordFixture.createRecordRequest(nonExistentUserId, 101L, 5000, 1800);

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ApiException.class, () -> {
            recordService.storeRecord(request);
        });
    }
}