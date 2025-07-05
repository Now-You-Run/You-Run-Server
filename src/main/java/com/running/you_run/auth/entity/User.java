package com.running.you_run.auth.entity;

import com.running.you_run.auth.Enum.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "app_user")
@Getter
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String email;
    @Column
    private String name;
    @Column

    @Setter
    private String nickname;
    @Column

    private String refreshToken;
    @Column
    private LocalDateTime refreshTokenExpiryDate;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    // MyPage 관련 정보들
    @Setter
    @Column
    private String profileImageUrl;

    @Setter
    @Column
    private LocalDate birthDate;

    @Setter
    @Column
    private Double height;

    @Setter
    @Column
    private Double weight;

    // 러닝 관련 데이터 (경험치 부분)
    @Setter
    @Column(columnDefinition = "int default 1")
    private Integer level;

    @Setter
    @Column
    private String grade;

    @Setter
    @Column(columnDefinition = "double default 0.0")
    private Double totalDistance;

    @PrePersist
    public void prePersist() {
        if (this.totalDistance == null) this.totalDistance = 0.0;
        if (this.level == null) this.level = 1;
    }

    public User() {

    }
}
