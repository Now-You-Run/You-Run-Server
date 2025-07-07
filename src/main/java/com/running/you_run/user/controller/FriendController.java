package com.running.you_run.user.controller;

import com.running.you_run.global.payload.Response;
import com.running.you_run.user.entity.User;
import com.running.you_run.user.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;
    @PostMapping("")
    public ResponseEntity<?> addFriend(@RequestParam Long senderId, @RequestParam Long otherId){
        User friend = friendService.addFriend(senderId, otherId);
        return Response.ok(friend);
    }
    @GetMapping("/accept")
    public ResponseEntity<?> acceptFriend(@RequestParam Long senderId, @RequestParam Long otherId){
        friendService.acceptFriend(senderId, otherId);
        return Response.ok("success");
    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFriend(@RequestParam Long senderId, @RequestParam Long otherId){
        friendService.deleteFriend(senderId, otherId);
        return Response.ok("success");
    }
    @GetMapping("/reject")
    public ResponseEntity<?> rejectFriend(@RequestParam Long senderId, @RequestParam Long otherId){
        friendService.rejectFriend(senderId, otherId);
        return Response.ok("success");
    }

    @GetMapping("/list")
    public ResponseEntity<?> findAllFriends(@RequestParam Long user1Id){
        List<User> userFriend = friendService.findUserFriend(user1Id);
        return Response.ok(userFriend);
    }
}
