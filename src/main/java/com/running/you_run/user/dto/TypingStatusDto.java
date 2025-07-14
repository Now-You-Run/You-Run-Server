package com.running.you_run.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypingStatusDto {
    private int userId;
    private boolean typing;
    private String roomId;
}