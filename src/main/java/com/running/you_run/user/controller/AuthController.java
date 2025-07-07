package com.running.you_run.user.controller;

import com.running.you_run.user.payload.response.UserLoginResponse;
import com.running.you_run.user.service.KakaoLoginService;
import com.running.you_run.global.payload.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/login/kakao")
    @Operation(
            summary = "카카오 로그인",
            description = "카카오 액세스 토큰으로 JWT 토큰을 발행합니다.\n"
    )
    public ResponseEntity<?> loginKakao(@RequestParam(name = "accessToken") String accessToken) {
        UserLoginResponse loginResponse = kakaoLoginService.login(accessToken);
        return Response.ok(loginResponse);
    }
//    @PostMapping("/register")
//    public Response registerUser(){
//
//    }


}
