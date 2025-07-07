package com.running.you_run.user.payload.dto;

import com.running.you_run.user.entity.Friend;
import com.running.you_run.user.entity.User;

public record FriendListItemDto(
        Long friendId,
        String name,
        String grade,
        int level
) {
    public static FriendListItemDto from(Friend friend, Long userId) {
        // Determine who is the friend
        User friendUser = friend.getUser1().getId().equals(userId) ? friend.getUser2() : friend.getUser1();
        return new FriendListItemDto(
                friendUser.getId(),
                friendUser.getName(),
                friendUser.getGrade().getName(),
                friendUser.getLevel()
        );
    }
}
