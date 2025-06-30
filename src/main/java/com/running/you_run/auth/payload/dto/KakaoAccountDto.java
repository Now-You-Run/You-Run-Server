package com.running.you_run.auth.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoAccountDto {

    // 프로필 정보 동의 여부
    @JsonProperty("profile_needs_agreement")
    private Boolean profileNeedsAgreement;

    // 닉네임, 프로필 사진 등의 프로필 정보
    private KakaoProfileDto profile;

    // 이메일 정보 동의 여부
    @JsonProperty("email_needs_agreement")
    private Boolean emailNeedsAgreement;

    // 이메일이 유효한지 여부
    @JsonProperty("is_email_valid")
    private Boolean isEmailValid;

    // 이메일이 검증되었는지 여부
    @JsonProperty("is_email_verified")
    private Boolean isEmailVerified;

    // 사용자 이메일
    private String email;
}

