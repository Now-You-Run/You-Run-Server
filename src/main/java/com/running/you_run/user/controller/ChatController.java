package com.running.you_run.user.controller;

import com.running.you_run.user.dto.ChatMessage;
import com.running.you_run.user.dto.ReadReceiptDto;
import com.running.you_run.user.dto.TypingStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    // 방별 메시지 in-memory 저장 (추후 DB 연동시 대체 가능)
    private final Map<String, List<ChatMessage>> chatRooms = new ConcurrentHashMap<>();

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        String roomId = chatMessage.getRoomId();
        String destination = "/topic/room/" + roomId;

        log.info("📩 Received message for room: {}, message: {}", roomId, chatMessage);

        // 본인이 읽음 상태로 초기화
        if (chatMessage.getReadBy() == null) {
            chatMessage.setReadBy(new ArrayList<>());
        }
        if (!chatMessage.getReadBy().contains(chatMessage.getSenderId())) {
            chatMessage.getReadBy().add(chatMessage.getSenderId());
        }

        // 메시지 저장
        chatRooms.computeIfAbsent(roomId, k -> new ArrayList<>()).add(chatMessage);

        // 구독자에게 전송
        messagingTemplate.convertAndSend(destination, chatMessage);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage,
                        SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        log.info("👤 User joined: {}", chatMessage.getSender());
    }

    @MessageMapping("/chat.typing")
    public void typingStatus(@Payload TypingStatusDto typingStatus) {
        String destination = "/topic/room/" + typingStatus.getRoomId() + "/typing";
        log.info("✏️ Typing status: {}", typingStatus);
        messagingTemplate.convertAndSend(destination, typingStatus);
    }

    @MessageMapping("/chat.read")
    public void readMessage(@Payload ReadReceiptDto readReceipt) {
        String roomId = readReceipt.getRoomId();
        int userId = readReceipt.getUserId();

        List<ChatMessage> messages = chatRooms.getOrDefault(roomId, new ArrayList<>());

        log.info("👁️‍🗨️ Read request: roomId={}, userId={}", roomId, userId);

        for (ChatMessage msg : messages) {
            if (!msg.getReadBy().contains(userId)) {
                msg.getReadBy().add(userId);
            }
        }

        // ✅ 메시지 업데이트 후 구독자에게 재전송하여 실시간 읽음 상태 갱신
        messagingTemplate.convertAndSend("/topic/room/" + roomId, messages);

        // 필요시 별도의 read-receipt topic으로 전송하여 UI 알림에도 활용 가능
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId + "/read-receipt",
                readReceipt
        );
    }
}