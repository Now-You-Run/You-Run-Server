package com.running.you_run.running.controller;

import com.running.you_run.global.payload.Response;
import com.running.you_run.running.payload.request.RunningTrackStoreRequest;
import com.running.you_run.running.payload.dto.TrackInfoDto;
import com.running.you_run.running.payload.response.TrackRecordResponse;
import com.running.you_run.running.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/gps")
@RestController
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @PostMapping("/track")
    public ResponseEntity<?> saveTrack(@RequestBody RunningTrackStoreRequest request) {
        trackService.storeTrack(request);
        return Response.ok("success");
    }

    @GetMapping("/track")
    public ResponseEntity<?> returnTrack(@RequestParam Long trackId) {
        TrackRecordResponse trackRecordResponse = trackService.returnTrackRecordResponse(trackId);
        return Response.ok(trackRecordResponse);
    }


}
