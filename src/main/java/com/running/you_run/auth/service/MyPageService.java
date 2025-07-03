package com.running.you_run.auth.service;

import com.running.you_run.auth.dto.UserProfileDto;
import com.running.you_run.auth.entity.User;
import com.running.you_run.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;

    public UserProfileDto getUserProfile(Long userId) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);
        return new UserProfileDto(user.getId(), user.getEmail(), user.getNickname(), user.getRole());
    }

    public UserProfileDto updateUserProfile(Long userId, UserProfileDto updateRequest) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);

        user.setNickname(updateRequest.getNickname());

        userRepository.save(user);

        return new UserProfileDto(user.getId(), user.getEmail(), user.getNickname(), user.getRole());
    }

    public UserProfileDto createUserProfile(UserProfileDto request) {
        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .role(request.getUserRole())
                .build();

        User savedUser = userRepository.save(user);

        return new UserProfileDto(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), savedUser.getRole());
    }

}
