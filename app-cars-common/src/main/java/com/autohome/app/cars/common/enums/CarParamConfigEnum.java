package com.autohome.app.cars.common.enums;

/**
 * 参数配置枚举
 */
public enum CarParamConfigEnum {
    SEAT(28, "座位数"),
    FUEL_TYPE(55, "燃料类型"),
    DISPPLACEMENT(40, "排量"),
    FLOW_MODE(41, "进气形式"),
    DRIVING_MODE_NAME(86, "驱动方式"),
    TRANSMISSION(108, "变速箱"),
    EMISSION_STANDARDS(61, "环保标准"),
    ELECTRIC_MOTORGROSS_POWER(63, "电动机总工率"),
    STRUCT(24, "车身结构");

    private int value;

    private String name;

    CarParamConfigEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
