package com.autohome.app.cars.common.utils;

public class Level {
    public static boolean isCVLevel(int levelId) {
        if (levelId == 11
                || levelId == 12
                || levelId == 13
                || levelId == 14
                || levelId == 25
        )
            return true;
        return false;
    }
}
