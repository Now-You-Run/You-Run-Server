package com.running.you_run.user.payload.dto;

public record TokenDto(
        String accessToken,
        String refreshToken
) {
}
