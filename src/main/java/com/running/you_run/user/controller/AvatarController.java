package com.running.you_run.user.controller;

import com.running.you_run.user.entity.User;
import com.running.you_run.user.repository.UserRepository;
import com.running.you_run.user.service.AvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avatars")
@RequiredArgsConstructor
public class AvatarController {
    private final AvatarService avatarService;
    private final UserRepository userRepository;

    // TODO: 실제 환경에서는 Principal 또는 인증 정보를 통해 User 객체를 받아와야 함

    // 1. 모든 아바타 목록 조회 (소유 여부 포함)
    @GetMapping
    public ResponseEntity<List<AvatarService.AvatarWithOwnershipDto>> getAllAvatars() {
        User user = userRepository.findById(1L).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(avatarService.getAllAvatarsWithOwnership(user));
    }

    // 2. 아바타 구매
    @PostMapping("/{avatarId}/purchase")
    public ResponseEntity<Void> purchaseAvatar(@PathVariable Long avatarId) {
        User user = userRepository.findById(1L).orElseThrow(() -> new RuntimeException("User not found"));
        avatarService.purchaseAvatar(user, avatarId);
        return ResponseEntity.ok().build();
    }

    // 3. 선택 아바타 변경
    @PostMapping("/{avatarId}/select")
    public ResponseEntity<Void> selectAvatar(@PathVariable Long avatarId) {
        User user = userRepository.findById(1L).orElseThrow(() -> new RuntimeException("User not found"));
        avatarService.selectAvatar(user, avatarId);
        return ResponseEntity.ok().build();
    }
} 