package com.running.you_run.user.service;

import com.running.you_run.user.Enum.FriendStatus;
import com.running.you_run.user.entity.Friend;
import com.running.you_run.user.entity.User;
import com.running.you_run.user.repository.FriendRepository;
import com.running.you_run.user.repository.UserRepository;
import com.running.you_run.global.exception.ApiException;
import com.running.you_run.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Transactional
    public void addFriend(Long user1Id, Long user2Id) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        boolean exists = friendRepository.existsByUser1IdAndUser2Id(user1Id, user2Id) ||
                friendRepository.existsByUser1IdAndUser2Id(user2Id, user1Id);
        if (exists) {
            throw new ApiException(ErrorCode.FRIEND_ALREADY_EXIST);
        }

        Friend friend = Friend.builder()
                .user1(user1)
                .user2(user2)
                .senderId(user1Id)
                .status(FriendStatus.WAITING)
                .build();
        friendRepository.save(friend);
    }
    @Transactional
    public void deleteFriend(Long user1Id, Long user2Id) {
        userRepository.findById(user1Id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        userRepository.findById(user2Id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        Optional<Friend> friendOpt = friendRepository
                .findByUser1IdAndUser2Id(user1Id, user2Id)
                .or(() -> friendRepository.findByUser1IdAndUser2Id(user2Id, user1Id));
        Friend friend = friendOpt.orElseThrow(() -> new ApiException(ErrorCode.FRIEND_NOT_EXIST));
        if (friend.delete()){
            friendRepository.save(friend);
            return;
        }
        throw new ApiException(ErrorCode.FRIEND_CAN_NOT_DELETE);
    }

    @Transactional
    public List<User> findUserFriend(Long userId){
        userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        // user1 또는 user2가 userId이고, status가 FRIEND인 모든 친구 관계 조회
        List<Friend> userFriends = friendRepository.findAllByUser1IdAndStatus(userId, FriendStatus.FRIEND);
        // 친구 User 객체만 추출
        List<User> friends = userFriends.stream()
                .map(f -> f.getUser1().getId().equals(userId) ? f.getUser2() : f.getUser1())
                .toList();
        // TODO: rank 등 추가 정보가 필요하면 friends를 DTO로 변환
        return friends;
    }

    @Transactional
    public void acceptFriend(Long user1Id, Long user2Id) {
        // user2가 user1의 친구 요청을 수락
        userRepository.findById(user1Id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        userRepository.findById(user2Id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        Optional<Friend> friendOpt = friendRepository
                .findByUser1IdAndUser2Id(user1Id, user2Id)
                .or(() -> friendRepository.findByUser1IdAndUser2Id(user2Id, user1Id));
        Friend friend = friendOpt.orElseThrow(() -> new ApiException(ErrorCode.FRIEND_NOT_EXIST));
        if (friend.getStatus() != FriendStatus.WAITING) {
            throw new ApiException(ErrorCode.FRIEND_CAN_NOT_ACCEPT);
        }
        friend.changeStatus(FriendStatus.FRIEND);
        friendRepository.save(friend);
    }

    @Transactional
    public void rejectFriend(Long user1Id, Long user2Id) {
        // user2가 user1의 친구 요청을 거절
        userRepository.findById(user1Id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        userRepository.findById(user2Id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        Optional<Friend> friendOpt = friendRepository
                .findByUser1IdAndUser2Id(user1Id, user2Id)
                .or(() -> friendRepository.findByUser1IdAndUser2Id(user2Id, user1Id));
        Friend friend = friendOpt.orElseThrow(() -> new ApiException(ErrorCode.FRIEND_NOT_EXIST));
        if (friend.getStatus() != FriendStatus.WAITING) {
            throw new ApiException(ErrorCode.FRIEND_CAN_NOT_REJECT);
        }
        friend.changeStatus(FriendStatus.REJECTED);
        friendRepository.save(friend);
    }
}
