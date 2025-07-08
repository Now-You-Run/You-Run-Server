package com.running.you_run.user.util;

import com.running.you_run.user.Enum.UserGrade;
import com.running.you_run.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LevelCalculator {
    private final Map<Integer, Double> levelDistanceMap = new HashMap<>();

    public LevelCalculator(){
        initLevelDistanceMap();
    }

    public int addDistanceAndLevelUp(double beforeUserDistance, double distanceKm) {
        return calculateLevelByTotalDistanceBinarySearch(beforeUserDistance + distanceKm);
    }

    public double getTotalDistanceForLevel(int level) {
        return levelDistanceMap.getOrDefault(level, Double.MAX_VALUE);
    }

    public double getDistanceToLevelUp(int level) {
        for (UserGrade grade : UserGrade.values()) {
            if (level >= grade.getMinLevel() && level <= grade.getMaxLevel()) {
                return grade.getLevelMultiple();
            }
        }
        return Double.MAX_VALUE;
    }
    private int calculateLevelByTotalDistanceBinarySearch(double totalDistance) {
        int left = 1;
        int right = 1000;

        while (left < right) {
            int mid = (left + right + 1) / 2;
            double requiredDistance = getTotalDistanceForLevel(mid);

            if (totalDistance >= requiredDistance) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }
        return left;
    }
    private void initLevelDistanceMap() {
        double sum = 0;
        levelDistanceMap.put(1, 0.0);
        for (int i = 2; i <= 1000; i++) {
            sum += getDistanceToLevelUp(i - 1);
            levelDistanceMap.put(i, sum);
        }
    }
}
