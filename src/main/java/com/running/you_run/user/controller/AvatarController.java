package com.running.you_run.user.controller;

import com.running.you_run.user.entity.User;
import com.running.you_run.user.entity.Avatar;
import com.running.you_run.user.repository.UserRepository;
import com.running.you_run.user.service.AvatarService;
import com.running.you_run.user.dto.AvatarWithOwnershipDto;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.running.you_run.user.dto.AvatarDto;

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
    public ResponseEntity<List<AvatarWithOwnershipDto>> getAllAvatars() {
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


    @GetMapping("/current")
    public ResponseEntity<?> getCurrentAvatar() {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Avatar currentAvatar = user.getSelectedAvatar();
        if (currentAvatar == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("현재 선택된 아바타가 없습니다.");
        }

        AvatarDto dto = new AvatarDto(
                currentAvatar.getId(),
                currentAvatar.getName(),
                currentAvatar.getImageUrl(),
                currentAvatar.getGlbUrl(),
                currentAvatar.getPrice(),
                currentAvatar.getGender() != null ? currentAvatar.getGender().name() : null
        );
        return ResponseEntity.ok(dto);
    }


} 