package com.running.you_run.user.entity;

import com.running.you_run.user.Enum.UserGrade;
import com.running.you_run.user.Enum.UserRole;
import com.running.you_run.user.payload.request.UserUpdateProfileReqeust;
import com.running.you_run.user.util.LevelCalculator;
import com.running.you_run.user.util.PointCalculator;
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
    private String refreshToken;
    @Column
    private LocalDateTime refreshTokenExpiryDate;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    @Setter
    @Column
    private LocalDate birthDate;

    @Setter
    @Column
    private Double height;

    @Setter
    @Column
    private Double weight;

    @Column(columnDefinition = "BIGINT default 0")
    private long point;

    @Column(columnDefinition = "int default 1")
    private Integer level;

    @Column
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserGrade grade = UserGrade.IRON;

    @Column
    private Long totalDistance;

    @PrePersist
    public void prePersist() {
        if (this.totalDistance == null) this.totalDistance = 0L;
        if (this.level == null) this.level = 1;
    }

    public User() {

    }

    public void updateUserProfile(UserUpdateProfileReqeust reqeust){
        this.name = reqeust.name();
        this.birthDate = reqeust.birthDate();
        this.height = reqeust.height();
        this.weight = reqeust.weight();
    }

    public void applyRunningResult(long distance, long point, int newLevel){
        this.totalDistance += distance;
        this.point += point;
        this.level = newLevel;
        this.grade = UserGrade.fromTotalDistance(this.level);
    }
}
