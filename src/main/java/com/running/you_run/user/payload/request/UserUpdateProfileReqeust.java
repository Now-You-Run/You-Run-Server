package com.running.you_run.user.payload.request;

import java.time.LocalDate;

public record UserUpdateProfileReqeust(
        Long userId,
        String name,
        LocalDate birthDate,
        Double height,
        Double weight
) {
}
