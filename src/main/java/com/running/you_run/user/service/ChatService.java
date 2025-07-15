package com.running.you_run.user.service;

import com.running.you_run.user.dto.ChatMessage;
import com.running.you_run.user.entity.ChatMessageEntity;
import com.running.you_run.user.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageEntity saveMessage(ChatMessage chatMessage) {
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setRoomId(chatMessage.getRoomId());
        entity.setSenderId(chatMessage.getSenderId());
        entity.setSender(chatMessage.getSender());
        entity.setContent(chatMessage.getContent());
        entity.setType(chatMessage.getType().name());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setReadBy(chatMessage.getReadBy() != null ? chatMessage.getReadBy() : new ArrayList<>());
        return chatMessageRepository.save(entity);
    }

    public List<ChatMessageEntity> getMessages(String roomId, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").ascending());
        return chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId, pageable);
    }
}
