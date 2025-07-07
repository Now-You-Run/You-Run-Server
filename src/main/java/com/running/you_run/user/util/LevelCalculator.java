package com.running.you_run.user.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LevelCalculator {
    private final Map<Integer, Double> levelDistanceMap = new HashMap<>();

    LevelCalculator(){
        initLevelDistanceMap();
    }

    public int addDistanceAndLevelUp(double beforeUserDistance, double distanceKm) {
        return calculateLevelByTotalDistanceBinarySearch(beforeUserDistance + distanceKm);
    }

    private double getTotalDistanceForLevel(int level) {
        return levelDistanceMap.getOrDefault(level, Double.MAX_VALUE);
    }

    private double getDistanceToLevelUp(int level) {
        if (level >= 1 && level <= 10) {
            return 3.0 * level;
        } else if (level >= 11 && level <= 50) {
            return 5.0 * level;
        } else if (level >= 51 && level <= 100) {
            return 10.0 * level;
        } else {
            return Double.MAX_VALUE;
        }
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
