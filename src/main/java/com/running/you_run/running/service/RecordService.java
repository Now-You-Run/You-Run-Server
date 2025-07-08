package com.running.you_run.running.service;

import com.running.you_run.running.Enum.RunningMode;
import com.running.you_run.running.entity.Record;
import com.running.you_run.running.entity.RunningTrack;
import com.running.you_run.running.payload.dto.RecordDto;
import com.running.you_run.running.payload.dto.TrackInfoDto;
import com.running.you_run.running.payload.request.RecordStoreRequest;
import com.running.you_run.running.repository.RecordRepository;
import com.running.you_run.running.repository.TrackRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecordService {
    private final RecordRepository recordRepository;
    private final TrackRepository trackRepository;
    @Transactional
    public Record storeRecord(RecordStoreRequest request){
        return recordRepository.save(request.toRecord());
    }
    @Transactional
    public List<RecordDto> findAllRecordById(Long userId){
        List<RecordDto> recordDtos = new ArrayList<>();
        List<Record> recordByUserId = recordRepository.findByUserId(userId);
        for (var record : recordByUserId){
            Optional<RunningTrack> byId = trackRepository.findById(record.getTrackId());
            if (byId.isEmpty()){
                continue;
            }
            recordDtos.add(RecordDto.from(record, TrackInfoDto.convertToResponseDto(byId.get())));
        }
        return recordDtos;
    }
}
