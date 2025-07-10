package com.running.you_run.user.util;

import org.springframework.stereotype.Component;

@Component
public class PointCalculator {
    // 100m당 지급되는 기본 포인트
    private static final int BASE_POINT_PER_100M = 2;
    // 1km마다 100m당 포인트가 얼마나 감소할지에 대한 계수
    private static final double REDUCTION_FACTOR_PER_KM = 0.1;
    public long calculatePoint(double distanceMeters) {
        // 1. 계산의 기본 단위 설정
        double distancePer100M = distanceMeters / 100.0;
        double distanceInKm = distanceMeters / 1000.0;

        // 2. 1km마다 감소율을 적용하여 '실질적인 100m당 획득 포인트' 계산
        double effectivePointRate = BASE_POINT_PER_100M - (distanceInKm * REDUCTION_FACTOR_PER_KM);

        // 3. 획득 포인트가 0보다 작아지는 것을 방지
        if (effectivePointRate < 0) {
            effectivePointRate = 0;
        }

        // 4. 최종 포인트 계산
        double calculatedPoints = distancePer100M * effectivePointRate;

        // 5. 소수점을 버리고 정수형으로 반환
        return (long) calculatedPoints;
    }
}
