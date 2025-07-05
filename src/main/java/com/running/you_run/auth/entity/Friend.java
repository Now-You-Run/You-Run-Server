package com.running.you_run.auth.entity;

import com.running.you_run.auth.Enum.FriendStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
public class Friend {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 친구 관계의 한쪽 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1Id", nullable = false)
    private User user1;

    // 친구 관계의 다른쪽 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2Id", nullable = false)
    private User user2;

    // 요청 보낸 사람의 id (user1 또는 user2의 id여야 함)
    @Column(nullable = false)
    private Long senderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus status;

    @Column(nullable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private LocalDateTime createdAt;

    public Friend() {
    }
    public boolean delete(){
        if (canDelete()){
            this.status = FriendStatus.DELETED;
            return true;
        }
        return false;
    }
    public void changeStatus(FriendStatus status) { this.status = status; }
    private boolean canDelete() {
        return this.status == FriendStatus.FRIEND;
    }

}

