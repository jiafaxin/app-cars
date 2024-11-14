package com.autohome.app.cars.service.components.recrank.enums;

import lombok.Getter;


@Getter
public enum SafetyLevelEnum {


    NONE(0, "-", "无"),
    EXCELLENT(1, "G", "优秀"),
    GOOD(2, "A", "良好"),
    GENERIC(3, "M", "一般"),

    BAD(4, "P", "较差");

    private final int key;
    private final String level;
    private final String desc;

    SafetyLevelEnum(int key, String level, String desc) {
        this.key = key;
        this.level = level;
        this.desc = desc;
    }


    public static SafetyLevelEnum getInstanceByKey(int key) {
        for (SafetyLevelEnum value : values()) {
            if (value.key == key) {
                return value;
            }
        }
        return null;
    }
}
