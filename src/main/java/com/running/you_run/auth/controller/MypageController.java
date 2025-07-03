package com.running.you_run.auth.controller;

import com.running.you_run.auth.dto.UserProfileDto;
import com.running.you_run.auth.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.running.you_run.auth.dto.MyPageSummaryDto;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long userId) throws NotFoundException {
        UserProfileDto profile = myPageService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileDto> updateUserProfile(@PathVariable Long userId,
                                                            @RequestBody UserProfileDto updateRequest) throws NotFoundException {
        UserProfileDto updated = myPageService.updateUserProfile(userId, updateRequest);
        return ResponseEntity.ok(updated);
    }

    @PostMapping
    public ResponseEntity<UserProfileDto> createUserProfile(@RequestBody UserProfileDto request) {
        UserProfileDto created = myPageService.createUserProfile(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{userId}/summary")
    public ResponseEntity<MyPageSummaryDto> getMyPageSummary(@PathVariable Long userId) {
        MyPageSummaryDto summary = myPageService.getMyPageSummary(userId);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/profile")
    public ResponseEntity<UserProfileDto> createProfile(@RequestBody UserProfileDto request) {
        UserProfileDto createdProfile = myPageService.createUserProfile(request);
        return ResponseEntity.ok(createdProfile);
    }
}
