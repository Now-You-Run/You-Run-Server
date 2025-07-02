package com.running.you_run.running.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
