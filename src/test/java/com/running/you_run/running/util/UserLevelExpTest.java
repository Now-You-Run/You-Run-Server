package com.running.you_run.running.util;

import com.running.you_run.user.Enum.UserGrade;
import com.running.you_run.user.entity.User;
import com.running.you_run.user.util.LevelCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserLevelExpTest {

    private LevelCalculator levelCalculator;

    @BeforeEach
    void setUp() {
        levelCalculator = new LevelCalculator();
    }

    @Test
    @DisplayName("경험치 추가 시 레벨과 등급이 올바르게 갱신된다")
    void gainExp_levelAndGradeUpdate() {
        // given
        User user = User.builder()
                .email("runner@example.com")
                .name("러너")
                .level(1)
                .grade(UserGrade.IRON)
                .totalDistance(0.0)
                .build();

        // when: 3km 추가 (레벨2 진입)
        user.gainExp(3.0, levelCalculator);

        // then
        assertEquals(2, user.getLevel());
        assertEquals(3.0, user.getTotalDistance());
        assertEquals(UserGrade.IRON, user.getGrade());

        // when: 48km 추가 (총 51km, 브론즈 진입)
        user.gainExp(48.0, levelCalculator);

        // then
        assertTrue(user.getLevel() > 2);
        assertEquals(51.0, user.getTotalDistance());
        assertEquals(UserGrade.BRONZE, user.getGrade());

        // when: 100km 추가 (총 151km, 실버 진입)
        user.gainExp(100.0, levelCalculator);

        // then
        assertEquals(151.0, user.getTotalDistance());
        assertEquals(UserGrade.SILVER, user.getGrade());
    }

    @Test
    @DisplayName("음수 거리나 0km 추가 시 레벨/등급/거리 변화 없음")
    void gainExp_zeroOrNegativeDistance() {
        User user = User.builder()
                .email("runner@example.com")
                .name("러너")
                .level(1)
                .grade(UserGrade.IRON)
                .totalDistance(0.0)
                .build();

        user.gainExp(0.0, levelCalculator);
        assertEquals(1, user.getLevel());
        assertEquals(0.0, user.getTotalDistance());
        assertEquals(UserGrade.IRON, user.getGrade());

        user.gainExp(-10.0, levelCalculator);
        assertEquals(1, user.getLevel());
        assertEquals(0.0, user.getTotalDistance());
        assertEquals(UserGrade.IRON, user.getGrade());
    }

    @Test
    @DisplayName("여러 번 gainExp 호출 시 누적 거리, 레벨, 등급이 누적 반영된다")
    void gainExp_multipleCalls() {
        User user = User.builder()
                .email("runner@example.com")
                .name("러너")
                .level(1)
                .grade(UserGrade.IRON)
                .totalDistance(0.0)
                .build();

        user.gainExp(10.0, levelCalculator); // 10km
        user.gainExp(20.0, levelCalculator); // 30km
        user.gainExp(30.0, levelCalculator); // 60km

        assertTrue(user.getLevel() > 1);
        assertEquals(60.0, user.getTotalDistance());
        assertTrue(user.getGrade() == UserGrade.IRON);
    }
}
