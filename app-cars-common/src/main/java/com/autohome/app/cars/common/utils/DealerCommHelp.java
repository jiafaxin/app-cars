package com.autohome.app.cars.common.utils;

public class DealerCommHelp {

    public static int getPriceShowFromWindowType(int windowType) {
        if (windowType == 1) {
            return 0;
        } else if (windowType == 2) {
            return 2;
        } else {
            return windowType + 2;
        }
    }
}
