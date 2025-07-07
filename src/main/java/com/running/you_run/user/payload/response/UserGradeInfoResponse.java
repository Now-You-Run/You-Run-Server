package com.running.you_run.user.payload.response;

import com.running.you_run.user.entity.User;

public record UserGradeInfoResponse(
        int level,
        String grade,
        Double totalDistance
) {
    public static UserGradeInfoResponse from(User user){
        return new UserGradeInfoResponse(
                user.getLevel(),
                user.getGrade().getName(),
                user.getTotalDistance()
        );
    }
}
