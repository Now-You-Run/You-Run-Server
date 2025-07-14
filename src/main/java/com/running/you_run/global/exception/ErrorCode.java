package com.running.you_run.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "700-1", "이미 존재하는 계정입니다."),
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "700-2", "존재하지 않는 계정입니다."),
    USER_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "700-3", "올바르지 않은 유저 형식입니다."),
    USER_INVALID_LOGIN(HttpStatus.BAD_REQUEST, "700-4", "올바르지 않은 로그인"),
    USER_UNAUTHORIZED(HttpStatus.FORBIDDEN, "700-5", "권한이 없습니다."),

    FRIEND_NOT_EXIST(HttpStatus.BAD_REQUEST, "710-1", "친구 관계가 아닙니다"),
    FRIEND_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "710-2", "이미 친구 관계입니다"),
    FRIEND_CAN_NOT_DELETE(HttpStatus.BAD_REQUEST, "710-3","친구 관계가 아니라 삭제할 수 없습니다"),
    FRIEND_CAN_NOT_REJECT(HttpStatus.BAD_REQUEST, "710-4","친구 관계가 아니라 거절할 수 없습니다."),
    FRIEND_CAN_NOT_ACCEPT(HttpStatus.BAD_REQUEST, "710-5","친구 관계가 아니라 수락할 수 없습니다"),

    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "701-1", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "701-2", "토큰이 만료되었습니다."),

    TRACK_NOT_EXIST(HttpStatus.BAD_REQUEST,"800-1","없는 트랙입니다."),
    INVALID_TRACK_PATH(HttpStatus.BAD_REQUEST,"800-2","유효하지 않은 트랙입니다."),
    INSUFFICIENT_TRACK_POINTS(HttpStatus.BAD_REQUEST, "800-3", "트랙 저장에 충분하지 않은 포인트 수입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}

