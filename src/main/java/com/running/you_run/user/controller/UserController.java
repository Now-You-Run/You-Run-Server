package com.running.you_run.user.controller;

import com.running.you_run.global.payload.Response;
import com.running.you_run.user.payload.request.UserGainExpRequest;
import com.running.you_run.user.payload.request.UserUpdateProfileReqeust;
import com.running.you_run.user.payload.response.UserGradeInfoResponse;
import com.running.you_run.user.payload.response.UserInfoResponse;
import com.running.you_run.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserProfileService userProfileService;

    @PatchMapping("")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserUpdateProfileReqeust userUpdateProfileReqeust) {
        UserInfoResponse userInfoResponse = userProfileService.updateUserProfile(userUpdateProfileReqeust);
        return Response.ok(userInfoResponse);
    }

    @PostMapping("/exp")
    public ResponseEntity<?> gainExpUser(@RequestBody UserGainExpRequest request) {
        UserGradeInfoResponse userGradeInfoResponse = userProfileService.gainExpUser(request);
        return Response.ok(userGradeInfoResponse);
    }

    @GetMapping("")
    public ResponseEntity<?> returnUserGradeInfo(@RequestParam Long userId) {
        UserGradeInfoResponse userGradeInfoResponse = userProfileService.returnUserGradeInfo(userId);
        return Response.ok(userGradeInfoResponse);
    }
}
