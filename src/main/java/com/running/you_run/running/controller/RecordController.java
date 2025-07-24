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

    @GetMapping("/{recordId}")
    @Operation(
            summary = "기록 단건 상세조회",
            description = "특정 recordId에 대한 상세정보를 반환합니다. (userPath 등 포함)"
    )
    public ResponseEntity<?> getRecordDetail(@PathVariable Long recordId) {
        RecordDto recordDto = recordService.findById(recordId); // 이 함수는 서비스에 구현해야 함
        return Response.ok(recordDto);
    }

}
