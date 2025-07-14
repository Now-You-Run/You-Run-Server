package com.running.you_run.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String sender;
    private int senderId;
    private String content;
    private MessageType type;
    private String roomId;
    private String createdAt;
    private List<Integer> readBy = new ArrayList<>();

    public enum MessageType {
        TALK, JOIN, LEAVE
    }

    // getters/setters
}

