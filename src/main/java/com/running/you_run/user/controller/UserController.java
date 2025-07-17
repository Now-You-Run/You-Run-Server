package com.running.you_run.user.controller;

import com.running.you_run.global.payload.Response;
import com.running.you_run.user.entity.FriendPointHistory;
import com.running.you_run.user.payload.request.UserGainExpRequest;
import com.running.you_run.user.payload.request.UserUpdateProfileReqeust;
import com.running.you_run.user.payload.response.UserGradeInfoResponse;
import com.running.you_run.user.payload.response.UserInfoResponse;
import com.running.you_run.user.repository.FriendPointHistoryRepository;
import com.running.you_run.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.running.you_run.user.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import com.running.you_run.user.payload.request.UpdateAveragePaceRequest;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserProfileService userProfileService;
    private final FriendPointHistoryRepository friendPointHistoryRepository;

    @PatchMapping("")
    @Operation(
            summary = "유저 정보 업데이트",
            description = "유저의 프로필에 해당하는 정보들을 업데이트 합니다.\n"
    )
    public ResponseEntity<?> updateUserProfile(@RequestBody UserUpdateProfileReqeust userUpdateProfileReqeust) {
        UserInfoResponse userInfoResponse = userProfileService.updateUserProfile(userUpdateProfileReqeust);
        return Response.ok(userInfoResponse);
    }

    @GetMapping("/grade")
    @Operation(
            summary = "등급과 관련된 유저의 정보 로드",
            description = "레벨,등급,경험치를 불러옵니다.\n"
    )
    public ResponseEntity<?> returnUserGradeInfo(@RequestParam Long userId) {
        UserGradeInfoResponse userGradeInfoResponse = userProfileService.returnUserGradeInfo(userId);
        return Response.ok(userGradeInfoResponse);
    }

    @GetMapping("")
    @Operation(
            summary = "유저의 정보 로드",
            description = "유저의 모든 정보를 불러옵니다.\n"
    )
    public ResponseEntity<?> returnUserInfo(@RequestParam Long userId) {
        UserInfoResponse userInfoResponse = userProfileService.returnUserProfile(userId);
        return Response.ok(userInfoResponse);
    }

    @PostMapping("{userId}/qr-refresh")
    public ResponseEntity<Response> refreshQrCode(@PathVariable Long userId) {
        String newCode = userProfileService.refreshQrCode(userId);
        return ResponseEntity.ok(Response.success(newCode));
    }

    @GetMapping("{userId}/code")
    public ResponseEntity<Response> getQrCode(@PathVariable Long userId) {
        String code = userProfileService.getQrCode(userId);
        return ResponseEntity.ok(Response.success(code));
    }

    @PatchMapping("/{receiverId}/point")
    public ResponseEntity<?> sendPoint(
            @PathVariable Long receiverId,
            @RequestParam Long senderId,
            @RequestParam Integer point
    ) {
        try {
            userProfileService.sendPoint(senderId, receiverId, point);

            Instant now = Instant.now(); // 현재 UTC 시각
            String nowString = now.toString(); // ISO 8601 문자열 (예: "2025-07-17T07:31:00Z")

            FriendPointHistory history = new FriendPointHistory();
            history.setSenderId(senderId);
            history.setReceiverId(receiverId);
            history.setPoint(point);
            history.setSentAt(Instant.now());

            friendPointHistoryRepository.save(history);

            return ResponseEntity.ok(Map.of(
                    "statuscode", "200",
                    "message", "포인트 전송 완료",
                    "sentAt", nowString
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "statuscode", "400",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "statuscode", "500",
                    "message", "서버 오류: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{userId}/point-history")
    public ResponseEntity<?> getPointHistory(@PathVariable Long userId) {
        List<FriendPointHistory> histories = friendPointHistoryRepository.findBySenderId(userId);

        List<Map<String, Object>> response = histories.stream()
                .map(h -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("receiverId", h.getReceiverId());
                    map.put("sentAt", h.getSentAt());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "statuscode", "200",
                "message", "포인트 전송 기록 조회 성공",
                "data", response
        ));
    }
    @PatchMapping("/average-pace")
    public ResponseEntity<Void> updateAveragePace(
            @RequestParam Long userId,
            @RequestBody UpdateAveragePaceRequest req
    ) {
        userProfileService.updateAveragePace(userId, req.averagePace());
        return ResponseEntity.noContent().build();
    }
}
