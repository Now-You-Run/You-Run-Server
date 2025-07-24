package com.running.you_run.global.exception.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.running.you_run.global.exception.ErrorCode;
import com.running.you_run.global.payload.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final ErrorCode DEFAULT_ERROR_CODE = ErrorCode.USER_UNAUTHORIZED;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.setStatus(DEFAULT_ERROR_CODE.getHttpStatus().value());
        response.setContentType("application/json; charset=UTF-8");
        Response<?> errorResponse = Response.error(DEFAULT_ERROR_CODE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
