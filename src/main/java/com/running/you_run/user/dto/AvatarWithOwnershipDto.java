package com.running.you_run.user.dto;

import com.running.you_run.user.entity.Avatar;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AvatarWithOwnershipDto {
    private Long id;
    private String name;
    private String imageUrl;
    private String glbUrl;
    private Integer price;
    private String gender;
    private boolean owned;

    public AvatarWithOwnershipDto(Avatar avatar, boolean owned) {
        this.id = avatar.getId();
        this.name = avatar.getName();
        this.imageUrl = avatar.getImageUrl();
        this.glbUrl = avatar.getGlbUrl();
        this.price = avatar.getPrice();
        this.gender = avatar.getGender() != null ? avatar.getGender().name() : null;
        this.owned = owned;
    }
}