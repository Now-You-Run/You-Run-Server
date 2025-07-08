package com.running.you_run.running.controller;

import com.running.you_run.global.payload.Response;
import com.running.you_run.running.payload.request.RunningTrackStoreRequest;
import com.running.you_run.running.payload.response.TrackListResponse;
import com.running.you_run.running.payload.response.TrackPagesResponse;
import com.running.you_run.running.payload.response.TrackRecordResponse;
import com.running.you_run.running.service.TrackService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/track")
@RestController
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @PostMapping("")
    @Operation(
            summary = "트랙 저장하기",
            description = "트랙을 저장합니다.\n"
    )
    public ResponseEntity<?> saveTrack(@RequestBody RunningTrackStoreRequest request) {
        trackService.storeTrack(request);
        return Response.ok("success");
    }

    @GetMapping("")
    @Operation(
            summary = "트랙 불러오기",
            description = "트랙을 불러옵니다.\n"
    )
    public ResponseEntity<?> returnTrack(@RequestParam Long trackId) {
        TrackRecordResponse trackRecordResponse = trackService.returnTrackRecordResponse(trackId);
        return Response.ok(trackRecordResponse);
    }

    @GetMapping("/list")
    @Operation(
            summary = "모든 트랙 불러오기",
            description = "모든 트랙을 불러옵니다.\n"
    )
    public ResponseEntity<?> returnAllTracks() {
        TrackListResponse trackListResponses = trackService.returnAllTrackRecordResponses();
        return Response.ok(trackListResponses);
    }

    @GetMapping("/list/order/close")
    public ResponseEntity<?> returnAllTracksByClose(
            @RequestParam double userLon,
            @RequestParam double userLat,
            @RequestParam int page,
            @RequestParam int size
    ) {
        TrackPagesResponse trackPagesResponse =
                trackService.returnAllTrackRecordResponsesOrderByDb(
                        page,
                        size,
                        userLon,
                        userLat
                );
        return Response.ok(trackPagesResponse);
    }

}
