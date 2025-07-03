package com.running.you_run.auth.service;

import com.running.you_run.auth.dto.UserProfileDto;
import com.running.you_run.auth.entity.User;
import com.running.you_run.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import com.running.you_run.auth.dto.MyPageSummaryDto;
import com.running.you_run.auth.repository.MyPageQueryRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final MyPageQueryRepository myPageQueryRepository;

    public UserProfileDto getUserProfile(Long userId) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);

        return UserProfileDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .birthDate(user.getBirthDate())
                .height(user.getHeight())
                .weight(user.getWeight())
                .userRole(user.getRole())
                .build();
    }

    public UserProfileDto updateUserProfile(Long userId, UserProfileDto updateRequest) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);

        user.setNickname(updateRequest.getNickname());
        user.setBio(updateRequest.getBio());
        user.setProfileImageUrl(updateRequest.getProfileImageUrl());
        user.setBirthDate(updateRequest.getBirthDate());
        user.setHeight(updateRequest.getHeight());
        user.setWeight(updateRequest.getWeight());

        userRepository.save(user);

        return UserProfileDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .birthDate(user.getBirthDate())
                .height(user.getHeight())
                .weight(user.getWeight())
                .userRole(user.getRole())
                .build();
    }

    @Transactional
    public UserProfileDto createUserProfile(UserProfileDto request) {
        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .bio(request.getBio())
                .profileImageUrl(request.getProfileImageUrl())
                .birthDate(request.getBirthDate())
                .height(request.getHeight())
                .weight(request.getWeight())
                .role(request.getUserRole())
                .build();

        User savedUser = userRepository.save(user);

        return UserProfileDto.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .bio(savedUser.getBio())
                .profileImageUrl(savedUser.getProfileImageUrl())
                .birthDate(savedUser.getBirthDate())
                .height(savedUser.getHeight())
                .weight(savedUser.getWeight())
                .userRole(savedUser.getRole())
                .build();
    }

    public MyPageSummaryDto getMyPageSummary(Long userId) {
        return myPageQueryRepository.getMyPageSummary(userId);
    }
}
