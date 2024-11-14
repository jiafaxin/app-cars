package com.autohome.app.cars.common.utils;

public class ThreadUtil {
    public static void sleep(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {

        }
    }
}
