package com.running.you_run.auth.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.running.you_run.auth.payload.dto.TokenDto;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserLoginResponse(
        boolean isRegistered,
        TokenDto tokens,
        String email,
        String username
) {
    public static UserLoginResponse loginSuccess(TokenDto tokenDto, String email, String username) {
        return new UserLoginResponse(
                true,
                tokenDto,
                email,
                username
        );
    }
}
