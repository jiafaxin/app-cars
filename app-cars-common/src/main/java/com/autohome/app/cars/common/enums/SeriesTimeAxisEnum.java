package com.autohome.app.cars.common.enums;

/**
 * @author : zzli
 * @description : 时间轴
 * @date : 2024/4/26 15:17
 */
public enum SeriesTimeAxisEnum {
    SPEC_PUBLISH_TIME("车型最新发布时间", 6666),
    PIC_FIRST_PUBLISH_TIME("图片首发", 101),
    PARAM_FIRST_PUBLISH_TIME("配置首发", 102),
    PIC_UPDATE_TIME("车图更新", 301),
    PARAM_UPDATE_TIME("配置更新", 302),
    new_car_sales_time("新车销量", 107),
    SERIES_ONLINE("新车上市", 103);

    private int value;
    private String type;

    public static String getTypeByValue(int value) {
        SeriesTimeAxisEnum[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            SeriesTimeAxisEnum type = var1[var3];
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

    private SeriesTimeAxisEnum(String type, int value) {
        this.type = type;
        this.value = value;
    }
}
