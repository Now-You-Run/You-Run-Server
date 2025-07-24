package com.running.you_run.user.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfoResponseDto {

    // 회원번호
    private Long id;

    // 카카오계정 정보
    @JsonProperty("kakao_account")
    private KakaoAccountDto kakaoAccountDto;

    // 추가 정보가 필요하다면 여기에 필드를 추가할 수 있습니다.
    // private Properties properties;
}
