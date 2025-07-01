package com.running.you_run.gps.controller;

import com.running.you_run.global.payload.Response;
import com.running.you_run.gps.payload.request.TrackStoreRequest;
import com.running.you_run.gps.payload.response.TrackPathResponse;
import com.running.you_run.gps.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/gps")
@RestController
@RequiredArgsConstructor
public class GpsController {

    private final TrackService trackService;

    @PostMapping("/track")
    public ResponseEntity<?> storeTrack(@RequestBody TrackStoreRequest request) {
        trackService.storeTrack(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/track")
    public ResponseEntity<?> returnTrack(@RequestParam Long trackId) {
        TrackPathResponse trackPathResponse = trackService.returnTrack(trackId);
        return Response.ok(trackPathResponse);
    }
}
