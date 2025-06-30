package com.running.you_run.auth.service;

import com.running.you_run.auth.Enum.UserRole;
import com.running.you_run.auth.entity.User;
import com.running.you_run.auth.payload.dto.KakaoUserInfoResponseDto;
import com.running.you_run.auth.payload.dto.TokenDto;
import com.running.you_run.auth.payload.response.UserLoginResponse;
import com.running.you_run.auth.repository.UserRepository;
import com.running.you_run.auth.util.KakaoApiClient;
import com.running.you_run.auth.util.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final KakaoApiClient kakaoApiClient;
    @Transactional
    public UserLoginResponse login(String kakaoAccessToken) {
        // 1. 외부 API 호출하여 사용자 정보 조회 (비즈니스 로직의 일부)
        KakaoUserInfoResponseDto userInfo = kakaoApiClient.fetchUserInfo(kakaoAccessToken);

        return processUserLogin(userInfo);
    }

    @Transactional
    public UserLoginResponse processUserLogin(KakaoUserInfoResponseDto userInfo) {
        String email = userInfo.getKakaoAccountDto().getEmail();
        User user = userRepository.findUserByEmail(email)
                .orElseGet(() -> registerNewUser(userInfo));

        TokenDto tokenDto = tokenProvider.createToken(email,user.getRole());
        return UserLoginResponse.loginSuccess(tokenDto,email,userInfo.getKakaoAccountDto().getProfile().getNickname());
    }

    private User registerNewUser(KakaoUserInfoResponseDto userInfo) {
        User newUser = User.builder()
                .email(userInfo.getKakaoAccountDto().getEmail())
                .name(userInfo.getKakaoAccountDto().getProfile().getNickname())
                .role(UserRole.USER)
                .build();
        return userRepository.save(newUser);
    }
}
