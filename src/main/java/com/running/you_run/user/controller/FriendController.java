package com.running.you_run.user.controller;

import com.running.you_run.global.payload.Response;
import com.running.you_run.user.entity.Friend;
import com.running.you_run.user.entity.User;
import com.running.you_run.user.payload.dto.FriendListItemDto;
import com.running.you_run.user.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
@Tag(name = "Friend", description = "친구 관련 기능")
public class FriendController {
    private final FriendService friendService;
    @PostMapping("")
    @Operation(summary = "친구 추가", description = "sender가 other에게 친구 추가 신청을 합니다.")
    public ResponseEntity<?> addFriend(@RequestParam Long senderId, @RequestParam Long otherId){
        User friend = friendService.addFriend(senderId, otherId);
        return Response.ok(friend);
    }
    @GetMapping("/accept")
    @Operation(summary = "친구 수락", description = "sender가 other의 친구 추가 신청을 합니다.")
    public ResponseEntity<?> acceptFriend(@RequestParam Long senderId, @RequestParam Long otherId){
        friendService.acceptFriend(senderId, otherId);
        return Response.ok("success");
    }
    @DeleteMapping("/delete")
    @Operation(summary = "친구 삭제", description = "sender가 other 친구를 삭제합니다.")
    public ResponseEntity<?> deleteFriend(@RequestParam Long senderId, @RequestParam Long otherId){
        friendService.deleteFriend(senderId, otherId);
        return Response.ok("success");
    }
    @GetMapping("/reject")
    @Operation(summary = "친구 거절", description = "sender가 other의 친구 신청을 거절합니다.")
    public ResponseEntity<?> rejectFriend(@RequestParam Long senderId, @RequestParam Long otherId){
        friendService.rejectFriend(senderId, otherId);
        return Response.ok("success");
    }

    @GetMapping("/list")
    @Operation(summary = "친구 목록 확인", description = "sender의 친구 목록을 반환합니다.")
    public ResponseEntity<?> findAllFriends(@RequestParam Long senderId){
        List<FriendListItemDto> userFriend = friendService.findUserFriends(senderId);
        return Response.ok(userFriend);
    }
    @GetMapping("/list/receive")
    @Operation(summary = "받은 친구 요청 목록 확인", description = "sender가 받은 친구 요청 목록을 반환합니다.")
    public ResponseEntity<?> findAllReceiveFriends(@RequestParam Long senderId){
        List<FriendListItemDto> userFriend = friendService.findReceivedFriendRequests(senderId);
        return Response.ok(userFriend);
    }

    @GetMapping("/list/sent")
    @Operation(summary = "보낸 친구 요청 목록 확인", description = "sender가 보낸 친구 요청 목록을 반환합니다.")
    public ResponseEntity<?> findAllSentFriends(@RequestParam Long senderId){
        List<FriendListItemDto> userFriend = friendService.findSentFriendRequests(senderId);
        return Response.ok(userFriend);
    }

    @PostMapping("/request-by-code")
    public ResponseEntity<String> sendFriendRequestByCode(
            @RequestParam Long senderId,
            @RequestParam String code) {
        try {
            friendService.sendFriendRequestByCode(senderId, code);
            return ResponseEntity.ok("친구 요청 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }
}
