package com.running.you_run.user.service;

import com.running.you_run.global.exception.ApiException;
import com.running.you_run.global.exception.ErrorCode;
import com.running.you_run.user.entity.Avatar;
import com.running.you_run.user.entity.User;
import com.running.you_run.user.entity.UserAvatar;
import com.running.you_run.user.repository.AvatarRepository;
import com.running.you_run.user.repository.UserAvatarRepository;
import com.running.you_run.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private final AvatarRepository avatarRepository;
    private final UserAvatarRepository userAvatarRepository;
    private final UserRepository userRepository;

    // 아바타 + 소유 여부 DTO
    @Getter
    @AllArgsConstructor
    public static class AvatarWithOwnershipDto {
        private Avatar avatar;
        private boolean owned;
    }

    // 1. 모든 아바타 목록 조회 (소유 여부 포함)
    public List<AvatarWithOwnershipDto> getAllAvatarsWithOwnership(User user) {
        List<Avatar> avatars = avatarRepository.findAll();
        List<UserAvatar> userAvatars = userAvatarRepository.findByUser(user);
        // 소유한 아바타 id 집합
        java.util.Set<Long> ownedAvatarIds = userAvatars.stream()
                .map(ua -> ua.getAvatar().getId())
                .collect(java.util.stream.Collectors.toSet());
        // DTO 변환
        return avatars.stream()
                .map(avatar -> new AvatarWithOwnershipDto(avatar, ownedAvatarIds.contains(avatar.getId())))
                .toList();
    }

    // 2. 사용자의 소유 아바타 목록 조회
    public List<UserAvatar> getUserAvatars(User user) {
        return userAvatarRepository.findByUser(user);
    }

    // 3. 아바타 구매
    @Transactional
    public void purchaseAvatar(User user, Long avatarId) {
        Avatar avatar = avatarRepository.findById(avatarId)
                .orElseThrow(() -> new ApiException(ErrorCode.AVATAR_NOT_EXIST));
        // 이미 소유한 경우
        if (userAvatarRepository.findByUserAndAvatar(user, avatar).isPresent()) {
            throw new ApiException(ErrorCode.AVATAR_ALREADY_OWNED);
        }
        // 포인트 부족
        if (user.getPoint() < avatar.getPrice()) {
            throw new ApiException(ErrorCode.AVATAR_INSUFFICIENT_POINT);
        }
        // 포인트 차감
        user.setPoint(user.getPoint() - avatar.getPrice());
        // 소유 추가
        UserAvatar userAvatar = UserAvatar.builder()
                .user(user)
                .avatar(avatar)
                .selected(false)
                .build();
        userAvatarRepository.save(userAvatar);
        // (선택) 첫 구매라면 선택 아바타로 설정
        if (user.getSelectedAvatar() == null) {
            user.setSelectedAvatar(avatar);
        }
    }

    // 4. 선택 아바타 변경
    @Transactional
    public void selectAvatar(User user, Long avatarId) {
        Avatar avatar = avatarRepository.findById(avatarId)
                .orElseThrow(() -> new ApiException(ErrorCode.AVATAR_NOT_EXIST));
        UserAvatar userAvatar = userAvatarRepository.findByUserAndAvatar(user, avatar)
                .orElseThrow(() -> new ApiException(ErrorCode.AVATAR_NOT_OWNED));
        // User의 선택 아바타 변경
        user.setSelectedAvatar(avatar);
        // 모든 UserAvatar의 selected false로 초기화 후, 해당 아바타만 true
        userAvatarRepository.findByUser(user).forEach(ua -> {
            ua.setSelected(ua.getAvatar().getId().equals(avatarId));
        });
    }
} 