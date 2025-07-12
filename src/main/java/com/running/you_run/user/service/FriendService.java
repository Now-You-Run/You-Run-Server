package com.running.you_run.user.service;

import com.running.you_run.user.Enum.FriendStatus;
import com.running.you_run.user.entity.Friend;
import com.running.you_run.user.entity.User;
import com.running.you_run.user.payload.dto.FriendListItemDto;
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
    public User addFriend(Long senderId, Long otherId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        User other = userRepository.findById(otherId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        boolean exists = friendRepository.existsByUser1IdAndUser2Id(senderId, otherId) ||
                friendRepository.existsByUser1IdAndUser2Id(otherId, senderId);
        if (exists) {
            throw new ApiException(ErrorCode.FRIEND_ALREADY_EXIST);
        }

        Friend friend = Friend.builder()
                .user1(sender)
                .user2(other)
                .senderId(senderId)
                .status(FriendStatus.WAITING)
                .build();
        friendRepository.save(friend);
        //친추 받은 대상 반환
        return other;
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
    public List<FriendListItemDto> findUserFriends(Long userId){
        userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        // user1 또는 user2가 userId이고, status가 FRIEND인 모든 친구 관계 조회
        List<Friend> userFriends = friendRepository
                .findAllByUser1IdAndStatusOrUser2IdAndStatus(userId, FriendStatus.FRIEND, userId, FriendStatus.FRIEND);

        // 친구 User 객체만 추출
        // TODO: rank 등 추가 정보가 필요하면 friends를 DTO로 변환
        return userFriends.stream()
                .map(friend -> FriendListItemDto.from(friend, userId))
                .toList();

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

    @Transactional
    public List<FriendListItemDto> findReceivedFriendRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));

        List<Friend> receivedRequests = friendRepository
                .findAllByUser2IdAndStatus(userId, FriendStatus.WAITING);

        return receivedRequests.stream()
                .map(friend -> FriendListItemDto.from(friend, userId))
                .toList();
    }
    @Transactional
    public List<FriendListItemDto> findSentFriendRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));

        List<Friend> sentRequests = friendRepository
                .findAllByUser1IdAndStatus(userId, FriendStatus.WAITING);

        return sentRequests.stream()
                .map(friend -> FriendListItemDto.from(friend, userId))
                .toList();
    }

    public void sendFriendRequestByCode(Long senderId, String code) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("보내는 유저가 존재하지 않습니다."));

        User receiver = userRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("해당 코드의 유저가 존재하지 않습니다."));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        // 이미 요청이 있는지 확인
        boolean alreadyRequested = friendRepository.existsBySenderIdAndUser2Id(sender.getId(), receiver.getId());
        if (alreadyRequested) {
            throw new IllegalArgumentException("이미 해당 유저에게 친구 요청을 보냈습니다.");
        }

        // 친구 요청 생성
        Friend friendRequest = Friend.builder()
                .user1(sender)
                .user2(receiver)
                .senderId(sender.getId())
                .status(FriendStatus.WAITING)
                .build();

        friendRepository.save(friendRequest);
    }
}
