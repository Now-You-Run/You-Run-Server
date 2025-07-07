package com.running.you_run.global.exception;

import com.running.you_run.global.payload.Response;
import com.running.you_run.running.controller.RecordController;
import com.running.you_run.running.controller.TrackController;
import com.running.you_run.user.controller.AuthController;
import com.running.you_run.user.controller.FriendController;
import com.running.you_run.user.controller.UserController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@RestControllerAdvice(annotations = {RestController.class}, basePackageClasses = {
//        RecordController.class,
//        TrackController.class,
//        AuthController.class,
//        FriendController.class,
//        UserController.class
//})
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String field = ex.getBindingResult().getFieldErrors().get(0).getField();
        String message = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        String finalErrorMessage = field + " : " + message;

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new Response("BAD_REQUEST", finalErrorMessage, null));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response> handleDataIntegrityException(DataIntegrityViolationException ex) {
        log.info(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new Response("DATA_INTEGRITY_ERROR", "DB 제약조건 위반: " + ex.getMostSpecificCause().getMessage(), null));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.info(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new Response("INVALID_ARGUMENT", ex.getMessage(), null));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception ex) {
        log.info(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response("INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.", null));
    }

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<Response> handleCustomException(ApiException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    private ResponseEntity<Response> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new Response(errorCode.getCode(), errorCode.getMessage(), null));
    }
}
