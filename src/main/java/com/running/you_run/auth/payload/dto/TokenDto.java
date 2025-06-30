package com.running.you_run.auth.payload.dto;

public record TokenDto(
        String accessToken,
        String refreshToken
) {
}
