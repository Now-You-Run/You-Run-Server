package com.running.you_run.auth.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.running.you_run.auth.payload.dto.KakaoUserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders; // Spring의 HttpHeaders를 사용해야 합니다.
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class KakaoApiClient {
    private static final String KAKAO_USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";
    private final RestTemplate restTemplate; // Bean으로 주입받아 사용

    public KakaoUserInfoResponseDto fetchUserInfo(String accessToken) {
        log.info("카카오 사용자 정보 조회를 시작합니다.");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(
                    KAKAO_USER_INFO_URI,
                    HttpMethod.GET,
                    requestEntity,
                    KakaoUserInfoResponseDto.class
            ).getBody();
        } catch (HttpClientErrorException e) {
            log.error("카카오 사용자 정보 요청 실패. 응답 코드: {}, 응답 본문: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("유효하지 않은 카카오 토큰입니다.", e);
        }
    }
}
