package com.running.you_run.user.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoProfileDto {
    private String nickname;

    // 프로필 이미지 URL (640x640)
    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    // 프로필 미리보기 이미지 URL (110x110)
    @JsonProperty("thumbnail_image_url")
    private String thumbnailImageUrl;
}
