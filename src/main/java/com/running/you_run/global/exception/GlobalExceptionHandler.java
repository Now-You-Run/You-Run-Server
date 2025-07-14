package com.running.you_run.global.exception;

import com.running.you_run.global.payload.Response;
import com.running.you_run.running.controller.RecordController;
import com.running.you_run.running.controller.TrackController;
import com.running.you_run.user.controller.AuthController;
import com.running.you_run.user.controller.FriendController;
import com.running.you_run.user.controller.UserController;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;

//
@RestControllerAdvice(basePackageClasses = {
        RecordController.class,
        TrackController.class,
        AuthController.class,
        FriendController.class,
        UserController.class
})
@ConditionalOnProperty(name = "swagger.exception.handler.enabled", havingValue = "false", matchIfMissing = true)
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
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

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<Response> handleCustomException(ApiException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        if (errorCode == null) {
            log.error("ApiException에서 ErrorCode가 null입니다.");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response("UNKNOWN_ERROR", "알 수 없는 오류가 발생했습니다.", null));
        }

        log.info("ApiException 발생: {}", errorCode.getMessage());
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(Exception.class)
    @Hidden
    public ResponseEntity<Response> handleException(Exception ex, HttpServletRequest request) throws Exception {
        String requestURI = request.getRequestURI();

        // Swagger 관련 요청은 완전히 제외
        List<String> swaggerPaths = Arrays.asList(
                "/v3/api-docs",
                "/swagger-ui",
                "/swagger-resources",
                "/webjars/",
                "/favicon.ico"
        );

        boolean isSwaggerRequest = swaggerPaths.stream()
                .anyMatch(requestURI::contains);

        if (isSwaggerRequest) {
            log.debug("Swagger 요청 제외: {}", requestURI);
            throw ex; // 원래 예외를 다시 던져서 Spring이 처리하도록
        }

        log.error("예상치 못한 예외 발생: URI={}, Exception={}", requestURI, ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response("INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.", null));
    }

    private ResponseEntity<Response> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new Response(errorCode.getCode(), errorCode.getMessage(), null));
    }
}
