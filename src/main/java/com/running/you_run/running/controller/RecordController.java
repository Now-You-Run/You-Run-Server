package com.running.you_run.running.controller;

import com.running.you_run.global.payload.Response;
import com.running.you_run.running.entity.Record;
import com.running.you_run.running.payload.request.RecordStoreRequest;
import com.running.you_run.running.payload.request.RunningTrackStoreRequest;
import com.running.you_run.running.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/record")
@RestController
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;
    @PostMapping("")
    public ResponseEntity<?> storeRecord(@RequestBody RecordStoreRequest request) {
        Record record = recordService.storeRecord(request);
        return Response.ok(record);
    }

    @GetMapping("")
    public ResponseEntity<?> loadAllRecordsByUserId(@RequestParam Long userId) {
        List<Record> allRecordById = recordService.findAllRecordById(userId);
        return Response.ok(allRecordById);
    }

}
