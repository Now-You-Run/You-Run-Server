package com.running.you_run.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistanceUpdateRequest {
    private Long userId;
    private double distanceKm;
}
