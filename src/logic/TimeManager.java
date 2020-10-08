package logic;

import levels.LevelHandler;

import static settings.UserSettings.displayTime;

public class TimeManager {

    public static final String TEXT_TOTAL_TIME = "TOTAL", TEXT_LEVEL_TIME = "LEVEL";
    private static long timeInLevelMillis, timeTotalMillis;

    public static void init() {
        timeInLevelMillis = 0;
    }

    public static void reset() {
        timeTotalMillis = 0;
    }

    public static void finishLevel() {
        if (LevelHandler.playerIsInPlayThrough())
            timeTotalMillis += timeInLevelMillis;
    }

    public static long getTimeInLevel() {
        return timeInLevelMillis;
    }

    public static long getTotalTime() {
        return timeTotalMillis + timeInLevelMillis;
    }

    public static void toggleTimeDisplay() {
        displayTime = !displayTime;
    }


    public static void update(int deltaTime) {
        timeInLevelMillis += deltaTime;
    }
}
