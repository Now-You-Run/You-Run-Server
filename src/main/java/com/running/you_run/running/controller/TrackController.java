package com.running.you_run.running.controller;

import com.running.you_run.global.payload.Response;
import com.running.you_run.running.payload.request.RunningTrackStoreRequest;
import com.running.you_run.running.payload.response.*;
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
        Long l = trackService.storeTrack(request);
        return Response.ok(new TrackStoreResponse(l));
    }

    @PostMapping("/admin")
    @Operation(
            summary = "서버 트랙 저장하기",
            description = "서버 트랙을 저장합니다.\n"
    )
    public ResponseEntity<?> saveServerTrack(@RequestBody RunningTrackStoreRequest request) {
        Long l = trackService.storeServerTrack(request);
        return Response.ok(new TrackStoreResponse(l));
    }

    @GetMapping("")
    @Operation(
            summary = "서버 트랙 상세 정보 불러오기",
            description = "서버 트랙 상세 정보를 불러옵니다.\n"
    )
    public ResponseEntity<?> returnServerTrack(@RequestParam Long trackId) {
        TrackRecordResponse trackRecordResponse = trackService.getServerTrackRecordResponse(trackId);
        return Response.ok(trackRecordResponse);
    }

    @GetMapping("/my")
    @Operation(
            summary = "마이 트랙 상세 정보 불러오기",
            description = "마이 트랙 상세 정보를 불러옵니다.\n"
    )
    public ResponseEntity<?> returnMyTrack(@RequestParam Long trackId) {
        MyTrackRecordListResponse trackRecordResponse = trackService.getMyTrackRecordResponse(trackId);
        return Response.ok(trackRecordResponse);
    }
    @DeleteMapping("/my")
    @Operation(
            summary = "마이 트랙 삭제",
            description = "마이 트랙 삭제.\n"
    )
    public ResponseEntity<?> deleteMyTrack(@RequestParam Long trackId,
                                           @RequestParam Long userId
    ) {
        trackService.deleteMyTrack(trackId, userId);
        return Response.ok(true);
    }

    @GetMapping("/list")
    @Operation(
            summary = "모든 트랙 불러오기",
            description = "모든 트랙을 불러옵니다.\n"
    )
    public ResponseEntity<?> returnAllTracks() {
        TrackListResponse trackListResponses = trackService.getAllTrackRecords();
        return Response.ok(trackListResponses);
    }

    @GetMapping("/list/order/close")
    @Operation(
            summary = "가까운 트랙 목록 불러오기",
            description = "사용자 위치에서 가장 가까운 트랙 목록을 불러옵니다. userId가 있으면 사용자 관련 정보를 포함할 수 있습니다."
    )
    public ResponseEntity<?> returnAllTracksByClose(
            @RequestParam double userLon,
            @RequestParam double userLat,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Long userId
    ) {
        TrackPagesResponse trackPagesResponse;
        if (userId != null) {
            trackPagesResponse = trackService.getUserTracksOrderByClose(
                    page, size, userId, userLon, userLat
            );
        } else {
            trackPagesResponse = trackService.getTracksOrderByClose(
                    page, size, userLon, userLat
            );
        }
        return Response.ok(trackPagesResponse);
    }

    @GetMapping("/list/order/dist")
    @Operation(
            summary = "총 길이로 정렬된 트랙 목록 불러오기",
            description = "총 길이로 정렬된 트랙 목록을 불러옵니다. userId가 있으면 사용자 관련 정보를 포함할 수 있습니다."
    )
    public ResponseEntity<?> returnAllTracksByClose(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String order,
            @RequestParam(required = false) Long userId
    ) {
        TrackPagesResponse trackPagesResponse = trackService.getUserTracksOrderByTotalDistance(
                page, size, order,userId
        );
        return Response.ok(trackPagesResponse);
    }


}
