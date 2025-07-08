package com.running.you_run.user.payload.request;

public record UserGainExpRequest(
        Long userId,
        double distance
) {
}
