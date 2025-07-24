package com.running.you_run.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestNotificationDto {
    private Long friendId;      // friend 테이블의 id
    private String name;        // 요청자 이름
    private int pendingCount;   // 현재 팬딩 개수
}
