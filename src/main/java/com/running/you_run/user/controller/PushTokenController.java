package com.running.you_run.user.controller;

import com.running.you_run.user.entity.User;
import com.running.you_run.user.repository.UserRepository;
import com.running.you_run.user.service.PushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.running.you_run.user.repository.UserPushTokenRepository;
import com.running.you_run.user.entity.UserPushToken;
import com.running.you_run.user.dto.PushTokenRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/push-token")
public class PushTokenController {
    @Autowired
    private UserPushTokenRepository userPushTokenRepository;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerPushToken(@RequestBody PushTokenRequest request) {
        UserPushToken token = userPushTokenRepository.findByUserId(request.getUserId())
                .orElse(new UserPushToken());

        token.setUserId(request.getUserId());
        token.setPushToken(request.getPushToken());

        userPushTokenRepository.save(token);

        return ResponseEntity.ok(Map.of(
                "statuscode", "200",
                "message", "푸시 토큰 등록 완료"
        ));
    }

    @PatchMapping("/{friendId}/cheer")
    public ResponseEntity<?> cheerFriend(
            @PathVariable Long friendId,
            @RequestParam Long senderId
    ) {
        UserPushToken friendPushToken = userPushTokenRepository.findByUserId(friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구의 푸시 토큰이 없습니다."));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("보낸 사람 정보를 찾을 수 없습니다."));
        String senderName = sender.getName();

        try {
            pushNotificationService.sendPushNotification(
                    friendPushToken.getPushToken(),
                    "러너 그라운드 응원 도착!",
                    senderName + "님이 당신을 응원합니다! 힘내세요!"
            );
            return ResponseEntity.ok(Map.of(
                    "statuscode", "200",
                    "message", "응원 알림 전송 완료"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "statuscode", "500",
                    "message", "알림 전송 실패: " + e.getMessage()
            ));
        }
    }
}
