package com.running.you_run.gps.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.LineString;

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

    private Long userId;

    @Column(columnDefinition = "geometry(LineString, 4326)")
    private LineString path;

    private String timestamp;
    private int distance;
}
