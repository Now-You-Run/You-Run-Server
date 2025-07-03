package com.running.you_run.running.service;

import com.running.you_run.running.Enum.RunningMode;
import com.running.you_run.running.entity.Record;
import com.running.you_run.running.payload.request.RecordStoreRequest;
import com.running.you_run.running.repository.RecordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordService {
    private final RecordRepository recordRepository;
    @Transactional
    public Record storeRecord(RecordStoreRequest request){
        return recordRepository.save(request.toRecord());
    }
    @Transactional
    public List<Record> findAllRecordById(Long userId){
        return recordRepository.findByUserId(userId);
    }
}
