package com.running.you_run.user.service;

import com.running.you_run.global.exception.ApiException;
import com.running.you_run.global.exception.ErrorCode;
import com.running.you_run.user.entity.PointTransaction;
import com.running.you_run.user.entity.User;
import com.running.you_run.user.payload.request.UserGainExpRequest;
import com.running.you_run.user.payload.request.UserUpdateProfileReqeust;
import com.running.you_run.user.payload.response.UserGradeInfoResponse;
import com.running.you_run.user.payload.response.UserInfoResponse;
import com.running.you_run.user.repository.PointTransactionRepository;
import com.running.you_run.user.repository.UserRepository;
import com.running.you_run.user.util.LevelCalculator;
//import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final LevelCalculator levelCalculator;
    private final PointTransactionRepository pointTransactionRepository;

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
    public UserGradeInfoResponse returnUserGradeInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        return UserGradeInfoResponse.from(user);
    }

    public String generateRandomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Transactional
    public String refreshQrCode(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        String newCode = generateRandomCode();
        user.setCode(newCode);
        user.setCodeUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return newCode;
    }

    @Transactional(readOnly = true)
    public String getQrCode(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        return user.getCode();
    }

    public User addPoint(Long userId, Integer pointToAdd) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        long currentPoint = user.getPoint(); // 항상 0 이상의 값

        user.setPoint(currentPoint + pointToAdd);

        return userRepository.save(user); // 트랜잭션 종료 시 자동 persist
    }

    @Transactional
    public void sendPoint(Long senderId, Long receiverId, Integer point) {
        LocalDateTime now = LocalDateTime.now();

        // 최근 보낸 기록 가져오기
        Optional<PointTransaction> lastTransactionOpt = pointTransactionRepository.findTopBySenderIdAndReceiverIdOrderBySentAtDesc(senderId, receiverId);

        if (lastTransactionOpt.isPresent()) {
            PointTransaction lastTransaction = lastTransactionOpt.get();
            LocalDateTime lastSentAt = lastTransaction.getSentAt();

            //if (lastSentAt.plusHours(12).isAfter(now)) {
            if (lastSentAt.plusMinutes(1).isAfter(now)) {
                // throw new IllegalStateException("포인트는 12시간 뒤에 다시 보낼 수 있습니다.");
                throw new IllegalStateException("포인트는 1분 뒤에 다시 보낼 수 있습니다.");
            }
        }

        // 포인트 차감 및 지급
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        if (sender.getPoint() < point) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }

        sender.setPoint(sender.getPoint() - point);
        receiver.setPoint(receiver.getPoint() + point);

        // 트랜잭션 기록 저장
        PointTransaction transaction = new PointTransaction();
        transaction.setSenderId(senderId);
        transaction.setReceiverId(receiverId);
        transaction.setPoint(point);
        transaction.setSentAt(now);

        pointTransactionRepository.save(transaction);
    @Transactional
    public void updateAveragePace(Long userId, Double pace) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        user.setAveragePace(pace);
        userRepository.save(user);
    }
}
