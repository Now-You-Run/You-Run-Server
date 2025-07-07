package com.running.you_run.user.payload.request;

import java.time.LocalDateTime;

public record UserUpdateProfileReqeust(
        String name,
        LocalDateTime birthDate,
        Double height,
        Double weight
) {
}
