package com.running.you_run.running.controller;

import com.running.you_run.global.payload.Response;
import com.running.you_run.running.entity.Record;
import com.running.you_run.running.payload.dto.RecordDto;
import com.running.you_run.running.payload.request.RecordStoreRequest;
import com.running.you_run.running.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/record")
@RestController
@RequiredArgsConstructor
@Tag(name = "Record", description = "경기 기록 저장 관련 기능")
public class RecordController {
    private final RecordService recordService;

    @PostMapping("")
    @Operation(
            summary = "기록 저장",
            description = "경기 결과를 저장합니다.\n" +
                    "mode는 BOT, MATCH 존재\n"
    )
    public ResponseEntity<?> storeRecord(@RequestBody RecordStoreRequest request) {
        Record record = recordService.storeRecord(request);
        return Response.ok(record);
    }

    @GetMapping("")
    @Operation(
            summary = "유저 기록 불러오기",
            description = "유저의 모든 경기 결과를 불러합니다.\n" +
                    "자유 모드 기록 반환 X(자유 모드 기록은 로컬에)\n"
    )
    public ResponseEntity<?> loadAllRecordsByUserId(@RequestParam Long userId) {
        List<RecordDto> allRecordById = recordService.findAllRecordById(userId);
        return Response.ok(allRecordById);
    }

}
