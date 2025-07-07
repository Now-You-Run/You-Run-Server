package com.running.you_run.user.Enum;

public enum UserGrade {
    IRON("아이언", 0, 50),
    BRONZE("브론즈", 50, 100),
    SILVER("실버", 100, 150),
    GOLD("골드", 150, 200),
    PLATINUM("플래티넘", 200, 250),
    DIAMOND("다이아", 250, 300),
    MASTER("마스터", 300, 450),
    GRAND_MASTER("그랜드 마스터", 450, 500),
    LEGEND_RUNNER("레전드 러너", 500, 700);

    private final String name;
    private final double minExp;
    private final double maxExp;

    UserGrade(String name, double minExp, double maxExp) {
        this.name = name;
        this.minExp = minExp;
        this.maxExp = maxExp;
    }

    public String getName() {
        return name;
    }

    public double getMinExp() {
        return minExp;
    }

    public double getMaxExp() {
        return maxExp;
    }

    /**
     * 누적 거리(distance)에 따라 등급을 반환합니다.
     */
    public static UserGrade fromTotalDistance(double totalDistance) {
        for (UserGrade grade : values()) {
            if (totalDistance > grade.minExp && totalDistance <= grade.maxExp) {
                return grade;
            }
        }
        // 700km 초과 시 최상위 등급 반환
        return LEGEND_RUNNER;
    }
}

