package com.example.roadrunnerfx;

public enum Levels {
    LEVEL_1("Level 1"), LEVEL_2("Level 2"), LEVEL_3("Level 3"), LEVEL_4("Level 4"), LEVEL_5("Level 5");

    private String levelName;
    private static Levels currentLevel;

    Levels(String levelName) {
        this.levelName = levelName;
    }

    public String getLevelName() {
        return levelName;
    }

    public static Levels getCurrentLevel() {
        return currentLevel;
    }

    public static void setStart() {
        currentLevel = LEVEL_1;
    }

    public static void level_up() {
        switch (currentLevel) {
            case LEVEL_1:
                currentLevel = LEVEL_2;
                break;
            case LEVEL_2:
                currentLevel = LEVEL_3;
                break;
            case LEVEL_3:
                currentLevel = LEVEL_4;
                break;
            case LEVEL_4:
                currentLevel = LEVEL_5;
                break;
            default:
                break;

        }
    }
}
