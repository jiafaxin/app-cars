package com.autohome.app.cars.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author zhangchengtao
 * @date 2024/8/22 19:48
 */
@RequiredArgsConstructor
@Getter
public enum NewSeriesHotTabEnum {
    SPEC(1,1, "车型"),
    COMMENT(21, 21, "评价"),
    PRICE(16, 16, "价格"),
    COMPARE(40003, 90005,"对比"),
    USED_CAR(18, 18, "二手车");

    public static NewSeriesHotTabEnum getByName(String typeName) {
        for (NewSeriesHotTabEnum value : values()) {
            if (value.typeName.equals(typeName)) {
                return value;
            }
        }
        return null;
    }

    private final Integer typeId;
    private final Integer moduleId;
    private final String typeName;
}
