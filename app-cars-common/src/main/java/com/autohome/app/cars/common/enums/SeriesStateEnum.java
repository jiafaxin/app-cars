package com.autohome.app.cars.common.enums;

/**
 * @author : zzli
 * @description : 车系/车型/年代款状态
 * @date : 2024/6/26 19:49
 */
public enum SeriesStateEnum {
    unlisted("未上市", 0),
    coming_soon("即将上市", 10),
    in_production_and_on_sale("在产在售", 20),
    discontinued_for_sale("停产在售", 30),
    halt_the_sales("停售", 40);

    private int value;
    private String type;

    public static String getTypeByValue(int value) {
        SeriesStateEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            SeriesStateEnum type = var1[var3];
            if (type.value == value) {
                return type.type;
            }
        }

        return "";
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    SeriesStateEnum(String type, int value) {
        this.type = type;
        this.value = value;
    }
}
