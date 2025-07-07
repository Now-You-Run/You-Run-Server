package com.running.you_run.user.service;

import com.running.you_run.global.exception.ApiException;
import com.running.you_run.global.exception.ErrorCode;
import com.running.you_run.user.entity.User;
import com.running.you_run.user.payload.request.UserGainExpRequest;
import com.running.you_run.user.payload.request.UserUpdateProfileReqeust;
import com.running.you_run.user.payload.response.UserGradeInfoResponse;
import com.running.you_run.user.payload.response.UserInfoResponse;
import com.running.you_run.user.repository.UserRepository;
import com.running.you_run.user.util.LevelCalculator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;
    private final LevelCalculator levelCalculator;
    @Transactional
    public UserInfoResponse updateUserProfile(UserUpdateProfileReqeust request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        user.updateUserProfile(request);
        userRepository.save(user);
        return UserInfoResponse.from(user);
    }
    @Transactional
    public UserInfoResponse returnUserProfile(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        return UserInfoResponse.from(user);
    }

    @Transactional
    public UserGradeInfoResponse gainExpUser(UserGainExpRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        user.gainExp(request.distance(), levelCalculator);
        userRepository.save(user);
        return UserGradeInfoResponse.from(user);
    }

    @Transactional
    public UserGradeInfoResponse returnUserGradeInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        return UserGradeInfoResponse.from(user);
    }

}
