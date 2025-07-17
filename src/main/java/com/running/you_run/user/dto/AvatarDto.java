package com.running.you_run.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AvatarDto {
    private Long id;
    private String name;
    private String imageUrl;
    private String glbUrl;
    private Integer price;
    private String gender;

}