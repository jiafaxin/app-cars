package com.autohome.app.cars.common.utils;

public class CarLevelUtil {

    public static String getLevelName(int levelId) {
        String result = "-1,其他";
        switch (levelId) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                result = "1,轿车";
                break;
            case 9:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
                result = "2,SUV";
                break;
            case 8:
            case 21:
            case 22:
            case 23:
            case 24:
                result = "3,MPV";
                break;
            case 7:
                result = "4,跑车";
                break;
            case 11:
                result = "6,微面";
                break;
            case 12:
                result = "7,微卡";
                break;
            case 13:
                result = "8,轻客";
                break;
            case 14:
            case 15:
                result = "5,皮卡";
                break;
            case 25:
                result = "9,轻卡";
                break;
            default:
                result = "-1,其他";
                break;
        }
        return result;
    }

}
