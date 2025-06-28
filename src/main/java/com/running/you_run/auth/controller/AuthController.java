package com.running.you_run.auth.controller;

import com.running.you_run.auth.payload.dto.TokenDto;
import com.running.you_run.auth.service.KakaoLoginService;
import com.running.you_run.global.payload.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/login/kakao")
    public ResponseEntity loginKakao(@RequestParam(name = "accessToken") String accessToken) {
        TokenDto loginResponse = kakaoLoginService.login(accessToken);
        return new ResponseEntity(loginResponse, HttpStatus.OK);
    }

    @GetMapping("/test")
    public Response connectionTest(){
        return new Response("201","hi","");
    }
}
