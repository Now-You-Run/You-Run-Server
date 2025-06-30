package com.running.you_run.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "social_account", uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "providerId"}))
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false)
    private String provider;
    @Column(nullable = false)
    private String providerId;
    @Column
    private String socialEmail;
    @Column
    private String socialName;
    @Column
    private String socialImageUrl;
}
