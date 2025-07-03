package com.running.you_run.global.payload;

import com.running.you_run.global.exception.ErrorCode;
import org.springframework.http.ResponseEntity;

public record Response<T>(
        String statuscode,
        String message,
        T data
){
    private static final String OK_CODE = "200";
    private static final String OK_MESSAGE = "ok";

    public static <T> ResponseEntity<Response<T>> ok(T data) {
        return ResponseEntity.ok(new Response<>(OK_CODE, OK_MESSAGE, data));
    }

    public static <T> ResponseEntity<Response<T>> ok(String message, T data) {
        return ResponseEntity.ok(new Response<>(OK_CODE, message, data));
    }
    public static <T> ResponseEntity<Response<T>> error(ErrorCode errorCode, String customMessage) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new Response<>(errorCode.getCode(), customMessage, null));
    }
    public static <T> Response<T> error(ErrorCode errorCode) {
        return new Response<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

}
