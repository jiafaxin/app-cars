package com.autohome.app.cars.service.services.enums;

import java.util.Arrays;

public enum SpecStateEnum {


    //车型销售状态
    State0("未售", 0),
    State10("即将销售", 10),
    State20("在售", 20),
    State30("停产在售", 30),
    State40("停售", 40);

    private String name;
    private Integer value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    SpecStateEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }


    public static SpecStateEnum getByValue(Integer value) {
        if (value != null) {
            for (SpecStateEnum stateEnum : SpecStateEnum.values()) {
                if (stateEnum.value == value) {
                    return stateEnum;
                }
            }
        }
        return null;
    }

    public static String getNameByValue(Integer value) {
        if (value != null) {
            for (SpecStateEnum stateEnum : SpecStateEnum.values()) {
                if (stateEnum.value == value) {
                    return stateEnum.name;
                }
            }
        }
        return "";
    }

    public static String getNameByValueForPic(Integer value) {
        if (value != null && Arrays.asList(0, 10, 40).contains(value)) {
            for (SpecStateEnum stateEnum : SpecStateEnum.values()) {
                if (stateEnum.value == value) {
                    return stateEnum.name;
                }
            }
        }
        return "";
    }

}
