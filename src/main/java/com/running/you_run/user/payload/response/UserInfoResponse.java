package com.running.you_run.user.payload.response;

import com.running.you_run.user.entity.User;

import java.time.LocalDate;

public record UserInfoResponse(
        String email,
        String name,
        LocalDate birthDate,
        Double height,
        Double weight,
        Double averagePace,
        int level,
        String grade,
        long totalDistance,
        long point
) {
    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
                user.getEmail(),
                user.getName(),
                user.getBirthDate(),
                user.getHeight(),
                user.getWeight(),
                user.getAveragePace(),
                user.getLevel(),
                user.getGrade().getName(),
                user.getTotalDistance(),
                user.getPoint()
        );
    }
}
