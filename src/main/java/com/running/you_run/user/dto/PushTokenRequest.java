package com.running.you_run.user.dto;

import lombok.Data;

@Data
public class PushTokenRequest {
    private Long userId;
    private String pushToken;
}
