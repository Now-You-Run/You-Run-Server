package com.running.you_run.running.entity;

import com.running.you_run.running.Enum.TrackStatus;
import com.running.you_run.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.LineString;

import java.time.LocalDateTime;

@Entity
@Getter // Lombok 어노테이션만 유지
@Table(name = "track")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RunningTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int totalDistance;
    private double rate;
    @Column(columnDefinition = "geometry(LineString, 4326)")
    private LineString path;
    private double startLatitude;
    private double startLongitude;
    private String address;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Setter
    private String thumbnailUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column
    @Enumerated(EnumType.STRING)
    @Setter
    private TrackStatus trackStatus;
}
