package com.running.you_run.user.dto;

public class PointSendResponseDto {
    private String message;
    private String sentAt; // ISO 8601 문자열로 반환

    public PointSendResponseDto(String message, String sentAt) {
        this.message = message;
        this.sentAt = sentAt;
    }

    public String getMessage() {
        return message;
    }

    public String getSentAt() {
        return sentAt;
    }
}