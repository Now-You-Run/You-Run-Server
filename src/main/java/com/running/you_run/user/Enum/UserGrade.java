    package com.running.you_run.user.Enum;

    public enum UserGrade {
        IRON("아이언", 1, 9,2),
        BRONZE("브론즈", 10, 19,2),
        SILVER("실버", 20, 29,3),
        GOLD("골드", 30, 39,3),
        PLATINUM("플래티넘", 40, 54,6),
        DIAMOND("다이아", 55, 79,6),
        MASTER("마스터", 80, 119,10),
        GRAND_MASTER("그랜드 마스터", 120, 179,10),
        LEGEND_RUNNER("레전드 러너", 180, 300,100);

        private final String name;
        private final int minLevel;
        private final int maxLevel;
        private final int levelMultiple;

        UserGrade(String name, int minLevel, int maxLevel, int levelMultiple) {
            this.name = name;
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
            this.levelMultiple = levelMultiple;
        }

        public String getName() {
            return name;
        }

        public double getMinLevel() {
            return minLevel;
        }

        public double getMaxLevel() {
            return maxLevel;
        }

        public int getLevelMultiple() {
            return levelMultiple;
        }

        /**
         * 누적 거리(distance)에 따라 등급을 반환합니다.
         */
        public static UserGrade fromTotalDistance(int level) {
            for (UserGrade grade : values()) {
                if (level > grade.minLevel && level <= grade.maxLevel) {
                    return grade;
                }
            }
            // 700km 초과 시 최상위 등급 반환
            return LEGEND_RUNNER;
        }
    }

