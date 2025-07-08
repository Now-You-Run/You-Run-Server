package com.running.you_run.running.util;

import com.running.you_run.user.Enum.UserGrade;
import com.running.you_run.user.util.LevelCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelCalculatorTest {

    private LevelCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new LevelCalculator();
    }

    @Test
    @DisplayName("각 레벨 구간별로 getDistanceToLevelUp이 올바른 곱셈 계수를 반환한다")
    void testGetDistanceToLevelUp() {
        // IRON: 1~9, 곱2
        for (int level = 1; level <= 9; level++) {
            assertEquals(2.0, calculator.getDistanceToLevelUp(level), "IRON 구간");
        }
        // BRONZE: 10~19, 곱2
        for (int level = 10; level <= 19; level++) {
            assertEquals(2.0, calculator.getDistanceToLevelUp(level), "BRONZE 구간");
        }
        // SILVER: 20~29, 곱3
        for (int level = 20; level <= 29; level++) {
            assertEquals(3.0, calculator.getDistanceToLevelUp(level), "SILVER 구간");
        }
        // GOLD: 30~39, 곱3
        for (int level = 30; level <= 39; level++) {
            assertEquals(3.0, calculator.getDistanceToLevelUp(level), "GOLD 구간");
        }
        // PLATINUM: 40~54, 곱6
        for (int level = 40; level <= 54; level++) {
            assertEquals(6.0, calculator.getDistanceToLevelUp(level), "PLATINUM 구간");
        }
        // DIAMOND: 55~79, 곱6
        for (int level = 55; level <= 79; level++) {
            assertEquals(6.0, calculator.getDistanceToLevelUp(level), "DIAMOND 구간");
        }
        // MASTER: 80~119, 곱10
        for (int level = 80; level <= 119; level++) {
            assertEquals(10.0, calculator.getDistanceToLevelUp(level), "MASTER 구간");
        }
        // GRAND_MASTER: 120~179, 곱10
        for (int level = 120; level <= 179; level++) {
            assertEquals(10.0, calculator.getDistanceToLevelUp(level), "GRAND_MASTER 구간");
        }
        // LEGEND_RUNNER: 180~300, 곱100
        for (int level = 180; level <= 300; level++) {
            assertEquals(100.0, calculator.getDistanceToLevelUp(level), "LEGEND_RUNNER 구간");
        }
        // 301 이상: Double.MAX_VALUE
        assertEquals(Double.MAX_VALUE, calculator.getDistanceToLevelUp(301));
    }

    @Test
    @DisplayName("엣지 케이스: 0레벨, 음수 레벨은 Double.MAX_VALUE 반환")
    void testEdgeCases() {
        assertEquals(Double.MAX_VALUE, calculator.getDistanceToLevelUp(0));
        assertEquals(Double.MAX_VALUE, calculator.getDistanceToLevelUp(-10));
    }
}
