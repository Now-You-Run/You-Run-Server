package com.running.you_run.user.util;

import com.running.you_run.user.Enum.UserGrade;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LevelCalculator {
    private final Map<Integer, Double> levelDistanceMap = new HashMap<>();

    public LevelCalculator() {
        initLevelDistanceMap();
    }

    // [수정] 파라미터 이름을 m 단위임을 명확히 합니다.
    public int calculateLevelByTotalDistance(double beforeDistanceMeters, double newDistanceMeters) {
        return calculateLevelByTotalDistanceBinarySearch(beforeDistanceMeters + newDistanceMeters);
    }

    public double getTotalDistanceForLevel(int level) {
        return levelDistanceMap.getOrDefault(level, Double.MAX_VALUE);
    }

    // [수정] 레벨업에 필요한 거리를 km가 아닌 m 단위로 반환합니다.
    public double getDistanceToLevelUp(int level) {
        for (UserGrade grade : UserGrade.values()) {
            if (level >= grade.getMinLevel() && level <= grade.getMaxLevel()) {
                // km를 m로 변환하여 반환
                return grade.getLevelMultiple() * 1000.0;
            }
        }
        return Double.MAX_VALUE;
    }

    // 이 메소드는 이제 m 단위를 기준으로 올바르게 동작합니다.
    private int calculateLevelByTotalDistanceBinarySearch(double totalDistanceMeters) {
        int left = 1;
        int right = 1000;

        while (left < right) {
            int mid = (left + right + 1) / 2;
            double requiredDistance = getTotalDistanceForLevel(mid);

            if (totalDistanceMeters >= requiredDistance) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }
        return left;
    }

    // 이 메소드는 이제 m 단위를 누적하여 저장합니다.
    private void initLevelDistanceMap() {
        double sum = 0;
        levelDistanceMap.put(1, 0.0);
        for (int i = 2; i <= 1000; i++) {
            sum += getDistanceToLevelUp(i - 1);
            levelDistanceMap.put(i, sum);
        }
    }
}
