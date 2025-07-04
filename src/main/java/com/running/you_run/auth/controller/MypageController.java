package com.running.you_run.auth.controller;

import com.running.you_run.auth.dto.MyPageDto;
import com.running.you_run.auth.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/{userId}")
    public ResponseEntity<MyPageDto> getUserProfile(@PathVariable Long userId) throws NotFoundException {
        MyPageDto profile = myPageService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<MyPageDto> updateUserProfile(@PathVariable Long userId,
                                                            @RequestBody MyPageDto updateRequest) throws NotFoundException {
        MyPageDto updated = myPageService.updateUserProfile(userId, updateRequest);
        return ResponseEntity.ok(updated);
    }

    @PostMapping
    public ResponseEntity<MyPageDto> createUserProfile(@RequestBody MyPageDto request) {
        MyPageDto created = myPageService.createUserProfile(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{userId}/summary")
    public ResponseEntity<MyPageDto> getMyPageSummary(@PathVariable Long userId) {
        MyPageDto summary = myPageService.getMyPageSummary(userId);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/profile")
    public ResponseEntity<MyPageDto> createProfile(@RequestBody MyPageDto request) {
        MyPageDto createdProfile = myPageService.createUserProfile(request);
        return ResponseEntity.ok(createdProfile);
    }
}
