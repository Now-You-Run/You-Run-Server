package com.running.you_run.user.entity;

import com.running.you_run.user.Enum.Gender;
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
import java.util.UUID;

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

    @Setter
    @Column
    @Builder.Default
    private Double averagePace = 0.0;

    @Column
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserGrade grade = UserGrade.IRON;

    @Column
    private Long totalDistance;

    @Column
    @Enumerated(EnumType.STRING)
    @Setter
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_avatar_id")
    @Setter
    private Avatar selectedAvatar;

    @Column(unique = true)
    private String code;

    @Column
    private LocalDateTime codeUpdatedAt;

    @PrePersist
    public void prePersist() {
        if (this.totalDistance == null) this.totalDistance = 0L;
        if (this.level == null) this.level = 1;
        if (this.code == null) this.code = generateRandomCode();
        if (this.averagePace == null) this.averagePace = 0.0;

    }

    public LocalDateTime getCodeUpdatedAt() {
        return codeUpdatedAt;
    }

    public void setCodeUpdatedAt(LocalDateTime codeUpdatedAt) {
        this.codeUpdatedAt = codeUpdatedAt;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String generateRandomCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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

    public void setPoint(long point) {
        this.point = point;
    }
}
