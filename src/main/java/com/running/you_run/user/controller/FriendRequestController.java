package com.running.you_run.user.controller;

import com.running.you_run.global.payload.Response;
import com.running.you_run.user.dto.FriendRequestNotificationDto;
import com.running.you_run.user.service.FriendRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendService;

    @PostMapping("/request")
    public ResponseEntity<Response<Void>> sendFriendRequest(
            @RequestParam Long senderId,
            @RequestParam Long receiverId
    ) {
        friendService.sendFriendRequest(senderId, receiverId);
        return Response.ok("친구 요청 완료", null);
    }

    @GetMapping("/pending")
    public ResponseEntity<Response<FriendRequestNotificationDto>> getPendingRequests(
            @RequestParam Long receiverId
    ) {
        FriendRequestNotificationDto dto = friendService.getPendingRequestInfo(receiverId);
        return Response.ok("팬딩 요청 개수 반환", dto);
    }
}
