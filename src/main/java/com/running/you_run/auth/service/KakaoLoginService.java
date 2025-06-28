package com.running.you_run.auth.service;

import com.running.you_run.auth.Enum.UserRole;
import com.running.you_run.auth.entity.User;
import com.running.you_run.auth.payload.dto.KakaoUserInfoResponseDto;
import com.running.you_run.auth.payload.dto.TokenDto;
import com.running.you_run.auth.repository.UserRepository;
import com.running.you_run.auth.util.KakaoApiClient;
import com.running.you_run.auth.util.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpHeaders;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final KakaoApiClient kakaoApiClient;
    @Transactional
    public TokenDto login(String kakaoAccessToken) {
        // 1. 외부 API 호출하여 사용자 정보 조회 (비즈니스 로직의 일부)
        KakaoUserInfoResponseDto userInfo = kakaoApiClient.fetchUserInfo(kakaoAccessToken);
        String email = userInfo.getKakaoAccountDto().getEmail(); // DTO 구조에 맞게

        // 2. 회원 확인 및 가입 (비즈니스 로직)
        User user = userRepository.findUserByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .role(UserRole.USER)
                            .build();
                    return userRepository.save(newUser);
                });
        return tokenProvider.createToken(user.getEmail(), user.getRole().toString());
    }

}
