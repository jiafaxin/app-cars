package com.autohome.app.cars.common.utils;

import java.text.DecimalFormat;

public class IntUtil {

    public static String convertToWan(int num) {
        return convertToWan(num, 2);
    }

    public static String convertToWan(int num, int decimalCount) {
        if (num < 10000) {
            return num + "";
        } else {
            return String.format("%." + decimalCount + "f", Double.valueOf(num / 10000.0)).toString() + "万";
        }
    }

    public static int tryParseInt(String val, int def) {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return def;
        }
    }
}
