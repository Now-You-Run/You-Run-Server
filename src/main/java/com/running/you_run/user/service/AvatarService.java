package com.running.you_run.user.service;

import com.running.you_run.user.entity.Avatar;
import com.running.you_run.user.entity.User;
import com.running.you_run.user.entity.UserAvatar;
import com.running.you_run.user.repository.AvatarRepository;
import com.running.you_run.user.dto.AvatarWithOwnershipDto;
import com.running.you_run.user.repository.UserAvatarRepository;
import com.running.you_run.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.running.you_run.global.exception.ApiException;
import com.running.you_run.global.exception.ErrorCode;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private final AvatarRepository avatarRepository;
    private final UserAvatarRepository userAvatarRepository;
    private final UserRepository userRepository;


    // 1. 모든 아바타 목록 조회 (소유 여부 포함)
    public List<AvatarWithOwnershipDto> getAllAvatarsWithOwnership(User user) {
        List<Avatar> avatars = avatarRepository.findAll();
        List<UserAvatar> userAvatars = userAvatarRepository.findByUser(user);
        java.util.Set<Long> ownedAvatarIds = userAvatars.stream()
                .map(ua -> ua.getAvatar().getId())
                .collect(java.util.stream.Collectors.toSet());
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

    public Avatar getCurrentAvatarByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        return user.getSelectedAvatar();
    }
}