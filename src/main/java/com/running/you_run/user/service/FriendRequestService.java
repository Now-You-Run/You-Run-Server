package com.running.you_run.user.service;

import com.running.you_run.user.Enum.FriendStatus;
import com.running.you_run.user.dto.FriendRequestNotificationDto;
import com.running.you_run.user.entity.Friend;
import com.running.you_run.user.entity.User;
import com.running.you_run.user.repository.FriendRequestRepository;
import com.running.you_run.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendRequestService {
    private final FriendRequestRepository friendRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void sendFriendRequest(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        Friend friend = Friend.builder()
                .user1(sender)
                .user2(receiver)
                .senderId(senderId)
                .status(FriendStatus.WAITING)
                .build();

        friendRepository.save(friend);

        Long pendingCount = friendRepository.countByUser2IdAndStatus(receiverId, FriendStatus.WAITING);

        FriendRequestNotificationDto notificationDto = new FriendRequestNotificationDto(
                friend.getId(),
                sender.getName(),
                pendingCount.intValue()
        );


        messagingTemplate.convertAndSend(
                "/topic/friend/" + receiverId,
                notificationDto
        );
    }

    public FriendRequestNotificationDto getPendingRequestInfo(Long receiverId) {
        Long pendingCount = friendRepository.countByUser2IdAndStatus(receiverId, FriendStatus.WAITING);
        return new FriendRequestNotificationDto(null, null, pendingCount.intValue());
    }
}
